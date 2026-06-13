package jndc_server.core;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCConfigCenter;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.message.DeviceSummary;
import jndc.core.message.OpenChannelMessage;
import jndc.core.message.ServiceControlMessage;
import jndc.core.message.TcpServiceDescription;
import jndc.utils.InetUtils;
import jndc.utils.ObjectSerializableUtils;
import jndc.utils.UUIDSimple;
import jndc.web_support.core.MessageNotificationCenter;
import jndc_server.core.port_app.ServerPortProtector;
import jndc_server.databases_object.ChannelContextCloseRecord;
import jndc_server.databases_object.ClientAuthRecord;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * server config center ,heart of this app
 */
@Slf4j
public class NDCServerConfigCenter implements NDCConfigCenter {

    public static final String DISCONNECT_REASON_CHANNEL_INACTIVE = "CHANNEL_INACTIVE";
    public static final String DISCONNECT_REASON_SERVER_CLOSED = "SERVER_CLOSED";
    public static final String DISCONNECT_REASON_HEARTBEAT_TIMEOUT = "HEARTBEAT_TIMEOUT";


    //客户端唯一编号作为key
    private Map<String, ChannelHandlerContextHolder> channelHandlerContextHolderMap = new ConcurrentHashMap<>();


    //服务端端口号 ： 端口监听上下文
    private Map<Integer, AsynchronousEventCenter.ServerPortBindContext> tcpRouter = new ConcurrentHashMap<>();


    /**
     * 通过客户端固定编号获取上下文，移除对应服务
     *
     * @param channelId
     * @param serverServiceDescriptions
     */
    public void removeServiceByChannelId(String channelId, List<ServerServiceDescription> serverServiceDescriptions) {
        if (channelId == null) {
            throw new RuntimeException("channelId is necessary");
        }

        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(channelId);
        if (channelHandlerContextHolder == null) {
            log.error("无法获取对应客户端上下文描述对象：" + channelId);
        } else {
            channelHandlerContextHolder.removeTcpServiceDescriptions(serverServiceDescriptions);
            notifyChannelRefresh();
        }
    }

    /**
     * 通过客户端唯一编号找到客户的上下文，进而注册客户端服务
     *
     * @param channelId
     * @param serverServiceDescriptions
     */
    public void addServiceByChannelId(String channelId, List<ServerServiceDescription> serverServiceDescriptions) {
        if (channelId == null) {
            throw new RuntimeException("channelId is necessary");
        }
        //获取id对应上下文
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(channelId);
        if (channelHandlerContextHolder == null) {
            log.error("can not found the holder that id is" + channelId);
        } else {
            //向上下文中添加服务
            channelHandlerContextHolder.addTcpServiceDescriptions(serverServiceDescriptions);
            notifyChannelRefresh();
        }

    }

    public void registerServiceProvider(ChannelHandlerContextHolder latestContext) {
        log.debug(latestContext.getContextIp() + " register " + latestContext.serviceNum() + " service");

        //这里的id就是客户端的唯一编号
        String id = latestContext.getClientId();
        ChannelHandlerContextHolder oldContext = channelHandlerContextHolderMap.get(id);
        if (oldContext == null) {
            //todo 不存在隧道
            log.debug("暂无已有隧道");

            //设置上下文描述对象
            channelHandlerContextHolderMap.put(id, latestContext);

        } else {
            if (!latestContext.checkSame(oldContext.getChannelHandlerContext())) {
                //todo 隧道和当前隧道来源不同
                log.info("释放非同源隧道:" + oldContext.getFingerprint() + "--->" + latestContext.getFingerprint());
                channelHandlerContextHolderMap.put(id, latestContext);
                log.info("放入:" + latestContext.getFingerprint());
                oldContext.releaseRelatedResources();
            }
        }


    }

    public void syncClientOnline(ChannelHandlerContextHolder holder, DeviceSummary deviceSummary) {
        if (holder == null) {
            return;
        }
        DBWrapper<ClientAuthRecord> dbWrapper = DBWrapper.getDBWrapper(ClientAuthRecord.class);
        ClientAuthRecord clientAuthRecord = loadOrCreateClientRecord(dbWrapper, holder.getClientId());
        clientAuthRecord.setAuthMode(holder.getAuthMode());
        clientAuthRecord.setLastClientIp(holder.getContextIp());
        clientAuthRecord.setLastClientPort(holder.getContextPort());
        clientAuthRecord.setLastSeenAt(holder.getLastHearBeatTimeStamp());
        clientAuthRecord.setLastOfflineAt(null);
        clientAuthRecord.applyDeviceSummary(deviceSummary);
        dbWrapper.updateByPrimaryKey(clientAuthRecord);
        notifyChannelRefresh();
    }

    public void syncClientOffline(ChannelHandlerContextHolder holder) {
        if (holder == null) {
            return;
        }
        DBWrapper<ClientAuthRecord> dbWrapper = DBWrapper.getDBWrapper(ClientAuthRecord.class);
        ClientAuthRecord clientAuthRecord = dbWrapper.customQuerySingle(
                "select * from client_auth_record where client_id=?",
                holder.getClientId()
        );
        if (clientAuthRecord == null) {
            clientAuthRecord = new ClientAuthRecord();
            clientAuthRecord.setClientId(holder.getClientId());
            clientAuthRecord.setClientAuthKey("");
            dbWrapper.insert(clientAuthRecord);
            clientAuthRecord = dbWrapper.customQuerySingle(
                    "select * from client_auth_record where client_id=?",
                    holder.getClientId()
            );
        }
        clientAuthRecord.setAuthMode(holder.getAuthMode());
        clientAuthRecord.setLastClientIp(holder.getContextIp());
        clientAuthRecord.setLastClientPort(holder.getContextPort());
        clientAuthRecord.setLastSeenAt(holder.getLastHearBeatTimeStamp());
        clientAuthRecord.setLastOfflineAt(System.currentTimeMillis());
        dbWrapper.updateByPrimaryKey(clientAuthRecord);
        notifyChannelRefresh();
    }

    private ClientAuthRecord loadOrCreateClientRecord(DBWrapper<ClientAuthRecord> dbWrapper, String clientId) {
        ClientAuthRecord clientAuthRecord = dbWrapper.customQuerySingle(
                "select * from client_auth_record where client_id=?",
                clientId
        );
        if (clientAuthRecord != null) {
            return clientAuthRecord;
        }

        clientAuthRecord = new ClientAuthRecord();
        clientAuthRecord.setClientId(clientId);
        clientAuthRecord.setClientAuthKey("");
        dbWrapper.insert(clientAuthRecord);
        return dbWrapper.customQuerySingle("select * from client_auth_record where client_id=?", clientId);
    }

    private void notifyChannelRefresh() {
        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
        if (messageNotificationCenter != null) {
            messageNotificationCenter.dateRefreshMessage("channel");
            messageNotificationCenter.dateRefreshMessage("serviceControl");
        }
    }

    public ChannelHandlerContextHolder getContextHolder(ChannelHandlerContext context) {
        if (context == null) {
            return null;
        }
        for (ChannelHandlerContextHolder holder : channelHandlerContextHolderMap.values()) {
            if (holder.checkSame(context)) {
                return holder;
            }
        }
        return null;
    }

    public ChannelHandlerContextHolder getContextHolder(String clientId) {
        return channelHandlerContextHolderMap.get(clientId);
    }

    public boolean isClientFullAuthorized(String clientId) {
        ChannelHandlerContextHolder holder = channelHandlerContextHolderMap.get(clientId);
        return holder != null && holder.getAuthMode() == OpenChannelMessage.FULL_AUTHORIZED;
    }

    public void applyControlledServices(String clientId, List<TcpServiceDescription> tcpServiceDescriptions) {
        ChannelHandlerContextHolder holder = channelHandlerContextHolderMap.get(clientId);
        if (holder == null) {
            log.info("client {} offline, skip controlled service sync", clientId);
            return;
        }
        if (holder.getAuthMode() != OpenChannelMessage.FULL_AUTHORIZED) {
            log.info("client {} is not full authorized, skip controlled service sync", clientId);
            return;
        }

        List<TcpServiceDescription> targetServices = tcpServiceDescriptions == null ? Collections.emptyList() : tcpServiceDescriptions;
        closeAffectedConnections(holder, targetServices);
        sendServiceControlSync(holder, targetServices);

        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
        messageNotificationCenter.dateRefreshMessage("serviceControl");
        messageNotificationCenter.dateRefreshMessage("services");
    }

    private void closeAffectedConnections(ChannelHandlerContextHolder holder, List<TcpServiceDescription> targetServices) {
        Map<String, ServerServiceDescription> currentMap = new HashMap<>();
        List<ServerServiceDescription> currentServices = holder.getTcpServiceDescriptions();
        if (currentServices != null) {
            currentServices.forEach(service -> currentMap.put(buildServiceKey(service), service));
        }

        Map<String, TcpServiceDescription> targetMap = new HashMap<>();
        targetServices.forEach(service -> targetMap.put(buildServiceKey(service), service));

        currentMap.forEach((key, currentService) -> {
            TcpServiceDescription targetService = targetMap.get(key);
            if (targetService == null || isServiceDefinitionChanged(currentService, targetService)) {
                currentService.resetActiveConnections();
            }
        });
    }

    private boolean isServiceDefinitionChanged(ServerServiceDescription currentService, TcpServiceDescription targetService) {
        if (targetService == null) {
            return true;
        }
        if (!safeEquals(currentService.getServiceName(), targetService.getServiceName())) {
            return true;
        }
        if (!safeEquals(currentService.getDescription(), targetService.getDescription())) {
            return true;
        }
        if (!safeEquals(currentService.getServiceIp(), targetService.getServiceIp())) {
            return true;
        }
        return currentService.getServicePort() != targetService.getServicePort();
    }

    private boolean safeEquals(Object left, Object right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }

    private String buildServiceKey(TcpServiceDescription service) {
        return service.getServiceIp() + ":" + service.getServicePort();
    }

    private void sendServiceControlSync(ChannelHandlerContextHolder holder, List<TcpServiceDescription> targetServices) {
        ServiceControlMessage serviceControlMessage = new ServiceControlMessage();
        serviceControlMessage.setClientId(holder.getClientId());
        serviceControlMessage.setTcpServiceDescriptions(targetServices);

        NDCMessageProtocol protocol = NDCMessageProtocol.of(
                InetUtils.localInetAddress,
                InetUtils.localInetAddress,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.SERVICE_CONTROL_SYNC
        );
        protocol.setData(ObjectSerializableUtils.object2bytes(serviceControlMessage));
        holder.getChannelHandlerContext().writeAndFlush(protocol);
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
            if (v.checkSame(inactive)) {
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
                    if (remove.getDisconnectReason() == null || "".equals(remove.getDisconnectReason().trim())) {
                        remove.setDisconnectReason(DISCONNECT_REASON_CHANNEL_INACTIVE);
                    }
                    syncClientOffline(remove);
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
//            MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);

            //推送不活动连接刷新
//            messageNotificationCenter.dateRefreshMessage("channelList");//notice the channel list refresh
//            messageNotificationCenter.dateRefreshMessage("serviceList");//notice the service list refresh
        } else {
            log.debug("无服务隧道...");
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
            channelHandlerContextHolder.setDisconnectReason(DISCONNECT_REASON_SERVER_CLOSED);
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
    public boolean addTCPRouter(int port, String enableDateRange, ServerServiceDescription serverServiceDescription) {
        AsynchronousEventCenter.ServerPortBindContext serverPortBindContext1 = tcpRouter.get(port);

        if (serverPortBindContext1 != null) {
            //todo exist a running context
            log.error("exist a context bind the port: " + port);
            AsynchronousEventCenter.ServerPortBindContext remove = tcpRouter.remove(port);
            if (remove != null) {
                remove.releaseRelatedResources();
            }
        }


        //创建服务端端口监听上下文（监听器+服务集合）
        AsynchronousEventCenter.ServerPortBindContext serverPortBindContext = new AsynchronousEventCenter.ServerPortBindContext(port);

        //设置注册服务
        serverPortBindContext.setServerServiceDescription(serverServiceDescription);

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
            serverPortBindContext.setServerServiceDescription(null);
            serverPortProtector.releaseRelatedResources();
            return false;
        }

        //设置端口监听器
        serverPortBindContext.setServerPortProtector(serverPortProtector);


        //将服务放入服务集合内
        //用于后续服务释放
        serverServiceDescription.addToServiceReleaseList(serverPortProtector);

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
        AsynchronousEventCenter.ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
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
                v.setDisconnectReason(DISCONNECT_REASON_HEARTBEAT_TIMEOUT);
                v.getChannelHandlerContext().close();
            }
        });
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
        AsynchronousEventCenter.ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
        if (serverPortBindContext == null) {
            //todo drop
            log.error("无法找到端口绑定上下文:" + serverPort);
            return;
        }

        //获取到端口上绑定的服务
        ServerServiceDescription tcpServiceDescription = serverPortBindContext.getServerServiceDescription();

        //获取隧道编号对应最新隧道
        //nat原因，可能持有旧的隧道未断开
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(tcpServiceDescription.getBindClientId());

        //刷新context
        if (channelHandlerContextHolder != null) {
            channelHandlerContextHolder.refreshContext(tcpServiceDescription);
        }


        //向服务发送消息
        tcpServiceDescription.sendMessage(ndcMessageProtocol);

        //异步执行中心
        TCPDataFlowAnalysisCenter TCPDataFlowAnalysisCenter = UniqueBeanManage.getBean(TCPDataFlowAnalysisCenter.class);
        TCPDataFlowAnalysisCenter.analyse(
                tcpServiceDescription.getBindClientId(),
                ndcMessageProtocol.copyWithData(),
                TCPDataFlowAnalysisCenter.DIRECTION_SERVER_TO_CLIENT
        );
    }


    @Override
    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {
        int serverPort = ndcMessageProtocol.getServerPort();
        AsynchronousEventCenter.ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
        if (serverPortBindContext == null) {
            //todo drop message
            log.info("drop the message cause the port" + serverPort + "has not be listened");
        } else {
            ServerServiceDescription serverServiceDescription = serverPortBindContext.getServerServiceDescription();
            if (serverServiceDescription != null) {
                TCPDataFlowAnalysisCenter TCPDataFlowAnalysisCenter = UniqueBeanManage.getBean(TCPDataFlowAnalysisCenter.class);
                TCPDataFlowAnalysisCenter.analyse(
                        serverServiceDescription.getBindClientId(),
                        ndcMessageProtocol.copyWithData(),
                        TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER
                );
            }
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
    public Map<Integer, AsynchronousEventCenter.ServerPortBindContext> getTcpRouter() {
        return tcpRouter;
    }
}
