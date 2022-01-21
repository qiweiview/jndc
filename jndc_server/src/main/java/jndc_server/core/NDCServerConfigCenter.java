package jndc_server.core;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCConfigCenter;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.utils.InetUtils;
import jndc.utils.UUIDSimple;
import jndc_server.databases_object.ChannelContextCloseRecord;
import jndc_server.web_support.core.MessageNotificationCenter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * server config center ,heart of this app
 */
@Slf4j
public class NDCServerConfigCenter implements NDCConfigCenter {


    //客户端唯一编号作为key
    private Map<String, ChannelHandlerContextHolder> channelHandlerContextHolderMap = new ConcurrentHashMap<>();


    //服务端端口号 ： 端口监听上下文
    private Map<Integer, ServerPortBindContext> tcpRouter = new ConcurrentHashMap<>();


    /**
     * 通过客户端固定编号获取上下文，移除对应服务
     *
     * @param channelId
     * @param tcpServiceDescriptionOnServers
     */
    public void removeServiceByChannelId(String channelId, List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers) {
        if (channelId == null) {
            throw new RuntimeException("channelId is necessary");
        }

        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(channelId);
        if (channelHandlerContextHolder == null) {
            log.error("无法获取对应客户端上下文描述对象：" + channelId);
        } else {
            channelHandlerContextHolder.removeTcpServiceDescriptions(tcpServiceDescriptionOnServers);
        }
    }

    /**
     * 通过客户端唯一编号找到客户的上下文，进而注册客户端服务
     *
     * @param channelId
     * @param tcpServiceDescriptionOnServers
     */
    public void addServiceByChannelId(String channelId, List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers) {
        if (channelId == null) {
            throw new RuntimeException("channelId is necessary");
        }
        //获取id对应上下文
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(channelId);
        if (channelHandlerContextHolder == null) {
            log.error("can not found the holder that id is" + channelId);
        } else {
            //向上下文中添加服务
            channelHandlerContextHolder.addTcpServiceDescriptions(tcpServiceDescriptionOnServers);
        }

    }

    public void registerServiceProvider(ChannelHandlerContextHolder channelHandlerContextHolder) {
        log.debug(channelHandlerContextHolder.getContextIp() + " register " + channelHandlerContextHolder.serviceNum() + " service");

        //这里的id就是客户端的唯一编号
        String id = channelHandlerContextHolder.getClientId();
        ChannelHandlerContextHolder exist = channelHandlerContextHolderMap.get(id);
        if (exist != null) {
            //todo 已经存该id的链接
            //执行更新

            //移除对应上下文描述对象
            ChannelHandlerContextHolder remove = channelHandlerContextHolderMap.remove(id);

            //释放上下文描述对象
            if (remove != null) {
                remove.releaseRelatedResources();
            }
        }

        //设置上下文描述对象
        channelHandlerContextHolderMap.put(id, channelHandlerContextHolder);

    }


    /**
     * 取消服务上下文描述
     *
     * @param inactive
     * @return
     */
    public void unRegisterContextHolder(ChannelHandlerContext inactive) {

        //获取移除客户端Id
        List<String> rmIds = new ArrayList<>();
        channelHandlerContextHolderMap.forEach((k, v) -> {
            if (v.contextBelong(inactive)) {
                rmIds.add(k);
            }
        });


        if (rmIds.size() > 0) {
            //todo 匹配到上下文描述对象

            //获取异步中心
            AsynchronousEventCenter asynchronousEventCenter = UniqueBeanManage.getBean(AsynchronousEventCenter.class);

            rmIds.stream().forEach(x -> {
                ChannelHandlerContextHolder remove = channelHandlerContextHolderMap.remove(x);

                //释放上下文描述对象
                if (remove != null) {
                    remove.releaseRelatedResources();
                }


                //创建日志
                ChannelContextCloseRecord channelContextCloseRecord = ChannelContextCloseRecord.of(remove);
                //写入断开日志
                asynchronousEventCenter.dbJob(() -> {
                    channelContextCloseRecord.setId(UUIDSimple.id());
                    channelContextCloseRecord.setTimeStamp(System.currentTimeMillis());
                    DBWrapper<ChannelContextCloseRecord> dbWrapper = DBWrapper.getDBWrapper(channelContextCloseRecord);
                    dbWrapper.insert(channelContextCloseRecord);
                });

            });

            //获取消息中心
            MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);

            //推送不活动连接刷新
            messageNotificationCenter.dateRefreshMessage("channelList");//notice the channel list refresh
            messageNotificationCenter.dateRefreshMessage("serviceList");//notice the service list refresh
        }


    }

    public void sendHeartBeat(String id) {
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(id);
        if (channelHandlerContextHolder == null) {
            log.error("未匹配到隧道:" + id);
            throw new RuntimeException("未匹配到隧道");

        } else {
            ChannelHandlerContext channelHandlerContext = channelHandlerContextHolder.getChannelHandlerContext();
            NDCMessageProtocol tqs = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.CHANNEL_HEART_BEAT);
            channelHandlerContext.writeAndFlush(tqs);
        }
    }


    public void unRegisterContextHolder(String id) {
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(id);
        if (channelHandlerContextHolder == null) {
            log.error("未匹配到隧道:" + id);
            throw new RuntimeException("未匹配到隧道");
        } else {
            channelHandlerContextHolder.getChannelHandlerContext().close();
        }
    }


    /**
     * bind local service to a server port
     *
     * @param port
     * @param enableDateRange
     * @return
     */
    public boolean addTCPRouter(int port, String enableDateRange, TcpServiceDescriptionOnServer tcpServiceDescriptionOnServer) {
        ServerPortBindContext serverPortBindContext1 = tcpRouter.get(port);

        if (serverPortBindContext1 != null) {
            //todo exist a running context
            log.error("exist a context bind the port: " + port);
            ServerPortBindContext remove = tcpRouter.remove(port);
            if (remove != null) {
                remove.releaseRelatedResources();
            }
        }


        //创建服务端端口监听上下文（监听器+服务集合）
        ServerPortBindContext serverPortBindContext = new ServerPortBindContext(port);

        //设置注册服务
        serverPortBindContext.setTcpServiceDescriptionOnServer(tcpServiceDescriptionOnServer);

        //创建端口监听器
        ServerPortProtector serverPortProtector = new ServerPortProtector(serverPortBindContext.getPort());

        //设置限制事件范围
        serverPortProtector.parseEnableDateRange(enableDateRange);


        //开启监听，此步骤是同步的
        boolean success = serverPortProtector.start();

        //check the port has been monitored or not
        if (!success) {
            //todo bind port fail
            //do rollback
            serverPortBindContext.setTcpServiceDescriptionOnServer(null);
            serverPortProtector.releaseRelatedResources();
            return false;
        }

        //设置端口监听器
        serverPortBindContext.setServerPortProtector(serverPortProtector);


        //将服务放入服务集合内
        //用于后续服务释放
        tcpServiceDescriptionOnServer.addToServiceReleaseList(serverPortProtector);

        //通过服务端监听端口放置监听上下文
        tcpRouter.put(port, serverPortBindContext);
        return true;

    }

    /**
     * interrupt  because accept the signal from service provider
     *
     * @param ndcMessageProtocol
     */
    public void connectionInterrupt(NDCMessageProtocol ndcMessageProtocol) {
        int serverPort = ndcMessageProtocol.getServerPort();
        ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
        if (serverPortBindContext == null) {
            //todo drop
            log.error("can not found the ServerPortBindContext for port :" + serverPort);
        } else {
            serverPortBindContext.connectionInterrupt(ndcMessageProtocol);
        }


    }

    /**
     * 刷新心跳时间
     *
     * @param channelId
     */
    public void refreshHeartBeatTimeStamp(String channelId) {
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(channelId);
        if (channelHandlerContextHolder != null) {
            //刷新心跳时间
            channelHandlerContextHolder.refreshHeartBeatTimeStamp();
        }

    }

    public void checkChannelHealthy() {
        channelHandlerContextHolderMap.forEach((k, v) -> {
            if (v.checkUnreachable()) {
                v.getChannelHandlerContext().close();
            }
        });
    }

    /**
     * 获取系统中所有服务
     *
     * @return
     */
    public List<TcpServiceDescriptionOnServer> getCurrentSupportService() {
        List<ChannelHandlerContextHolder> channelHandlerContextHolders = getChannelHandlerContextHolders();
        List<TcpServiceDescriptionOnServer> collect = channelHandlerContextHolders.stream()
                .flatMap(x -> x.getTcpServiceDescriptions().stream())
                .collect(Collectors.toList());
        return collect;
    }


    /**
     * inner
     *
     * @param <T>
     */
    private interface InnerCondition<T> {
        public boolean check(ChannelHandlerContextHolder channelHandlerContextHolder, T t);
    }


    /**
     * 添加消息至发送队列
     *
     * @param ndcMessageProtocol
     */
    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
        //服务端监听的端口
        int serverPort = ndcMessageProtocol.getServerPort();
        //通过端口获取 绑定的服务
        ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
        if (serverPortBindContext == null) {
            //todo drop
            log.error("无法找到端口绑定上下文:" + serverPort);
            return;
        }

        //获取到端口上绑定的服务
        TcpServiceDescriptionOnServer tcpServiceDescription = serverPortBindContext.getTcpServiceDescriptionOnServer();

        //获取隧道编号对应最新隧道
        //nat原因，可能持有旧的隧道未断开
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(tcpServiceDescription.getBindClientId());

        //刷新context
        channelHandlerContextHolder.refreshContext(tcpServiceDescription);

        //向服务发送消息
        tcpServiceDescription.sendMessage(ndcMessageProtocol);
    }


    @Override
    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {
        int serverPort = ndcMessageProtocol.getServerPort();
        ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
        if (serverPortBindContext == null) {
            //todo drop message
            log.info("drop the message cause the port" + serverPort + "has not be listened");
        } else {
            serverPortBindContext.receiveMessage(ndcMessageProtocol);
        }
    }

    /* ------------------getter setter------------------ */

    /**
     * get all ChannelHandlerContextHolder
     *
     * @return
     */
    public List<ChannelHandlerContextHolder> getChannelHandlerContextHolders() {
        List<ChannelHandlerContextHolder> list = new ArrayList<>();
        Collection<ChannelHandlerContextHolder> values1 = channelHandlerContextHolderMap.values();
        values1.forEach(x -> {
            list.add(x);
        });

        return list;
    }

    /**
     * 获取    --->   服务端端口号 ： 服务端口绑定上下文
     *
     * @return
     */
    public Map<Integer, ServerPortBindContext> getTcpRouter() {
        return tcpRouter;
    }
}
