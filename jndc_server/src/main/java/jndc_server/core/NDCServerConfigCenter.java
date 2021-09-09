package jndc_server.core;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCConfigCenter;
import jndc.core.NDCMessageProtocol;
import jndc.core.TcpServiceDescription;
import jndc.utils.InetUtils;
import jndc_server.databases_object.ChannelContextCloseRecord;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * server config center ,heart of this app
 */
@Slf4j
public class NDCServerConfigCenter implements NDCConfigCenter {


    //客户端唯一编号作为key
    private Map<String, ChannelHandlerContextHolder> channelHandlerContextHolderMap = new ConcurrentHashMap<>();


    //key 服务端监听端口
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
            log.error("can not found the holder that id is" + channelId);
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
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(channelId);
        if (channelHandlerContextHolder == null) {
            log.error("can not found the holder that id is" + channelId);
        } else {
            channelHandlerContextHolder.addTcpServiceDescriptions(tcpServiceDescriptionOnServers);
        }

    }

    public void registerServiceProvider(ChannelHandlerContextHolder channelHandlerContextHolder) {
        log.debug(channelHandlerContextHolder.getContextIp() + " register " + channelHandlerContextHolder.serviceNum() + " service");

        //这里的id就是客户端的唯一编号
        String id = channelHandlerContextHolder.getId();
        ChannelHandlerContextHolder exist = channelHandlerContextHolderMap.get(id);
        if (exist != null) {
            //执行更新
            updateContext(exist, channelHandlerContextHolder);
        }

        channelHandlerContextHolderMap.put(id, channelHandlerContextHolder);

    }

    private void updateContext(ChannelHandlerContextHolder oldContext, ChannelHandlerContextHolder newContext) {
        log.info("执行上下文替换...");
        Map<String, TcpServiceDescriptionOnServer> collect = newContext.getTcpServiceDescriptions().stream().collect(Collectors.toMap(x -> x.getBindClientId(), x -> x));


        //旧的服务端描述已经被tcpRouter装载，需要替换其上下文，通过唯一客户端主键
        List<TcpServiceDescriptionOnServer> tcpServiceDescriptions = oldContext.getTcpServiceDescriptions();
        tcpServiceDescriptions.forEach(x -> {
            String bindClientId = x.getBindClientId();

            //此处实际一个隧道中所有服务都使用同一个唯一客户端id
            TcpServiceDescriptionOnServer tcpServiceDescriptionOnServer = collect.get(bindClientId);
            if (tcpServiceDescriptionOnServer != null) {
                //执行隧道上下文替换
                x.setBelongContext(tcpServiceDescriptionOnServer.getBelongContext());
            }

        });

    }

    public ChannelContextCloseRecord unRegisterServiceProvider(ChannelHandlerContext inactive) {
        Set<Map.Entry<String, ChannelHandlerContextHolder>> entries = channelHandlerContextHolderMap.entrySet();
        Iterator<Map.Entry<String, ChannelHandlerContextHolder>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ChannelHandlerContextHolder> next = iterator.next();
            String key = next.getKey();
            ChannelHandlerContextHolder value = next.getValue();
            if (value.contextBelong(inactive)) {
                ChannelContextCloseRecord channelContextCloseRecord = ChannelContextCloseRecord.of(value);
                value.releaseRelatedResources();
                channelHandlerContextHolderMap.remove(key);
                return channelContextCloseRecord;
            }

        }
        log.error("未匹配到对应隧道");
        return null;
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


    public void unRegisterServiceProvider(String id) {
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
        if (tcpRouter.get(port) != null) {
            //todo exist a running context
            log.error("exist a context bind the port: " + port);
            return true;//return true to change the state to success
        }


        //创建服务端端口监听上下文（监听器+服务集合）
        ServerPortBindContext serverPortBindContext = new ServerPortBindContext(port);
        serverPortBindContext.setTcpServiceDescriptionOnServer(tcpServiceDescriptionOnServer);

        //端口监听对象，接收端口所有请求
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

        //将监听器设置到上下文中
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

    public void refreshHeartBeatTimeStamp(String channelId) {
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(channelId);
        if (channelHandlerContextHolder != null) {
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

    public List<TcpServiceDescription> getCurrentSupportService() {
        List<TcpServiceDescription> tcpServiceDescriptions = new ArrayList<>();
        List<ChannelHandlerContextHolder> channelHandlerContextHolders = getChannelHandlerContextHolders();
        channelHandlerContextHolders.forEach(x -> {
            tcpServiceDescriptions.addAll(x.getTcpServiceDescriptions());
        });

        return tcpServiceDescriptions;
    }


    /**
     * inner
     *
     * @param <T>
     */
    private interface InnerCondition<T> {
        public boolean check(ChannelHandlerContextHolder channelHandlerContextHolder, T t);
    }


    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
        //服务端监听的端口
        int serverPort = ndcMessageProtocol.getServerPort();
        //获取端口下绑定的服务
        ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
        if (serverPortBindContext == null) {
            //todo drop
            log.info("can not found bind service for port:" + serverPort);
            return;
        }

        //获取到端口上绑定的服务
        TcpServiceDescriptionOnServer tcpServiceDescription = serverPortBindContext.getTcpServiceDescriptionOnServer();
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

    public Map<Integer, ServerPortBindContext> getTcpRouter() {
        return tcpRouter;
    }
}
