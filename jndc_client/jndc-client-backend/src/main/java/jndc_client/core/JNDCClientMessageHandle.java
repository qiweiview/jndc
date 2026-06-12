package jndc_client.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.message.OpenChannelMessage;
import jndc.core.message.RegistrationMessage;
import jndc.core.message.ServiceControlMessage;
import jndc.core.message.TcpServiceDescription;
import jndc.core.message.UserError;
import jndc.exception.SecreteDecodeFailException;
import jndc.utils.ApplicationExit;
import jndc.utils.ObjectSerializableUtils;
import jndc_client.start.ClientStart;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JNDCClientMessageHandle extends SimpleChannelInboundHandler<NDCMessageProtocol> {

    private JNDCClient client;

    public static final String NAME = "NDC_CLIENT_HANDLE";

    private ChannelHandlerContext ctx;

    private volatile boolean reConnectTag = true;

    private volatile boolean forceControlledRegisterSync;

    public JNDCClientMessageHandle(JNDCClient jndcClient) {
        this.client = jndcClient;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        sendChannelOpenChannelMessage();
    }

    private void sendChannelOpenChannelMessage() throws Exception {
        JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);

        OpenChannelMessage openChannelMessage = new OpenChannelMessage();
        openChannelMessage.setChannelId(ClientStart.CLIENT_ID);
        openChannelMessage.setAuth(clientConfig.getSecrete());
        openChannelMessage.setClientAuthKey(ClientStart.CLIENT_AUTH_KEY);
        openChannelMessage.setAuthMode(clientConfig.getAuthMode());
        byte[] bytes = ObjectSerializableUtils.object2bytes(openChannelMessage);

        InetAddress unused = InetAddress.getLocalHost();
        NDCMessageProtocol tqs = NDCMessageProtocol.of(unused, unused, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.OPEN_CHANNEL);
        tqs.setData(bytes);


        //send data
        ctx.writeAndFlush(tqs);
    }


    public void stopRegister(ClientServiceDescription... list) {
        if (list == null || list.length < 1) {
            log.error("ignore empty list");
            return;
        }
        stopRegister(Stream.of(list).collect(Collectors.toList()));
    }

    public void startRegister(ClientServiceDescription... list) {
        if (list == null || list.length < 1) {
            log.error("ignore empty list");
            return;
        }
        startRegister(Stream.of(list).collect(Collectors.toList()));
    }

    public void stopRegister(List<ClientServiceDescription> list) {

        JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);
        JNDCClientConfigCenter jndcClientConfigCenter = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);

        if (list == null || list.size() < 1) {
            log.error("ignore empty list");
            return;
        }


        //get service info
        List<TcpServiceDescription> tcpServiceDescriptions = new ArrayList<>();
        list.forEach(x -> {
            TcpServiceDescription tcpServiceDescription = x.toTcpServiceDescription();
            tcpServiceDescriptions.add(tcpServiceDescription);
            jndcClientConfigCenter.destroyService(x);//destroy the support service
        });


        //create register message
        RegistrationMessage registrationMessage = new RegistrationMessage(RegistrationMessage.TYPE_UNREGISTER);
        registrationMessage.setChannelId(ClientStart.CLIENT_ID);
        registrationMessage.setAuth(clientConfig.getSecrete());


        registrationMessage.setTcpServiceDescriptions(tcpServiceDescriptions);
        try {
            sendRegistrationMessage(registrationMessage, NDCMessageProtocol.SERVICE_UNREGISTER);
        } catch (UnknownHostException e) {
            log.error(e + "");
        }
    }

    public void startRegister(List<ClientServiceDescription> list) {
        JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);
        JNDCClientConfigCenter jndcClientConfigCenter = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);

        if (list == null || list.size() < 1) {
            log.error("ignore empty list");
            return;
        }


        //get service info
        List<TcpServiceDescription> tcpServiceDescriptions = new ArrayList<>();
        list.forEach(x -> {
            if (x.isServiceEnable()) {
                TcpServiceDescription tcpServiceDescription = x.toTcpServiceDescription();
                tcpServiceDescriptions.add(tcpServiceDescription);
                if (!jndcClientConfigCenter.hasActiveService(x)) {
                    jndcClientConfigCenter.initService(x);//init the support service
                }

            } else {
                log.info("忽略未启用配置项:" + x.getServiceName());
            }
        });

        if (tcpServiceDescriptions.size() < 1) {
            log.info("配置内无任何可用服务，跳过发送注册消息...");
            return;
        }

        //create register message
        RegistrationMessage registrationMessage = new RegistrationMessage(RegistrationMessage.TYPE_REGISTER);
        registrationMessage.setChannelId(ClientStart.CLIENT_ID);
        registrationMessage.setAuth(clientConfig.getSecrete());


        registrationMessage.setTcpServiceDescriptions(tcpServiceDescriptions);
        try {
            sendRegistrationMessage(registrationMessage, NDCMessageProtocol.SERVICE_REGISTER);
        } catch (UnknownHostException e) {
            log.error(e + "");
        }


    }

    /**
     * send service register by config file
     *
     * @throws Exception
     */
    private void sendServiceRegisterMessage() {
        JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);


        if (clientConfig == null) {
            log.error("can not load service support config");
            return;
        }

        //get service list by config file
        List<ClientServiceDescription> clientServiceDescriptions = clientConfig.getClientServiceDescriptions();

//不再单独注册管理页面
//        final boolean[] addPage = {true};
//        clientServiceDescriptions.forEach(x -> {
//            if (ClientHttpManagement.CLIENT_MANAGEMENT.equals(x.getServiceName())) {
//                addPage[0] = false;
//            }
//        });
//        //添加管理页面到注册服务
//        if (addPage[0]) {
//            clientServiceDescriptions.add(ClientHttpManagement.DEPLOY_PORT);
//        }

        startRegister(clientServiceDescriptions);
        log.info("注册完成...");
    }

    private void handleOpenChannelResponse(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) throws Exception {
        OpenChannelMessage object = ndcMessageProtocol.getObject(OpenChannelMessage.class);
        JNDCClientConfigCenter jndcClientConfigCenter = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
        jndcClientConfigCenter.registerMessageChannel(channelHandlerContext);
        InetAddress unused = InetAddress.getLocalHost();

        //use the message with id
        final byte[] bytes = ObjectSerializableUtils.object2bytes(object);

        //use the message from server as heartbeat request message（每次创建新消息，避免复用同一对象）
        EventLoop eventExecutors = channelHandlerContext.channel().eventLoop();
        eventExecutors.scheduleWithFixedDelay(() -> {
            NDCMessageProtocol heartBeat = NDCMessageProtocol.of(unused, unused, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.CHANNEL_HEART_BEAT);
            heartBeat.setData(bytes);
            jndcClientConfigCenter.addMessageToSendQueue(heartBeat);
        }, 0, 60, TimeUnit.SECONDS);

        JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);
        if (clientConfig.getAuthMode() == OpenChannelMessage.FULL_AUTHORIZED) {
            forceControlledRegisterSync = true;
            log.info("全授权模式，等待服务端下发服务清单...");
            return;
        }

        log.info("准备启动服务注册...");
        sendServiceRegisterMessage();
    }

    private void sendRegistrationMessage(RegistrationMessage registrationMessage, byte messageType) throws UnknownHostException {
        byte[] bytes = ObjectSerializableUtils.object2bytes(registrationMessage);
        InetAddress unused = InetAddress.getLocalHost();
        NDCMessageProtocol tqs = NDCMessageProtocol.of(
                unused,
                unused,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.UN_USED_PORT,
                messageType
        );
        tqs.setData(bytes);
        ctx.writeAndFlush(tqs);
    }

    private void handleServiceControlSync(NDCMessageProtocol ndcMessageProtocol) {
        ServiceControlMessage serviceControlMessage = ndcMessageProtocol.getObject(ServiceControlMessage.class);
        JNDCClientConfigCenter configCenter = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);

        Map<String, ClientServiceDescription> currentServices = new HashMap<>();
        configCenter.getActiveServiceDescriptions().forEach(service -> currentServices.put(buildServiceKey(service), service));

        Map<String, ClientServiceDescription> targetServices = new HashMap<>();
        List<TcpServiceDescription> tcpServiceDescriptions = serviceControlMessage.getTcpServiceDescriptions();
        if (tcpServiceDescriptions != null) {
            tcpServiceDescriptions.forEach(service -> {
                ClientServiceDescription clientServiceDescription = toClientServiceDescription(service);
                targetServices.put(buildServiceKey(clientServiceDescription), clientServiceDescription);
            });
        }

        List<ClientServiceDescription> toRemove = new ArrayList<>();
        List<ClientServiceDescription> toAdd = new ArrayList<>();
        List<ClientServiceDescription> toReRegister = new ArrayList<>();

        currentServices.forEach((key, currentService) -> {
            ClientServiceDescription targetService = targetServices.get(key);
            if (targetService == null || isServiceChanged(currentService, targetService)) {
                toRemove.add(currentService);
            } else if (forceControlledRegisterSync) {
                toReRegister.add(currentService);
            }
        });

        targetServices.forEach((key, targetService) -> {
            ClientServiceDescription currentService = currentServices.get(key);
            if (currentService == null || isServiceChanged(currentService, targetService)) {
                toAdd.add(targetService);
            }
        });

        if (!toRemove.isEmpty()) {
            stopRegister(toRemove);
        }
        if (!toAdd.isEmpty()) {
            startRegister(toAdd);
        }
        if (!toReRegister.isEmpty()) {
            reRegister(toReRegister);
        }

        forceControlledRegisterSync = false;
        log.info("服务端控制服务同步完成，移除 {} 个，新增/变更 {} 个，重注册 {} 个", toRemove.size(), toAdd.size(), toReRegister.size());
    }

    private void reRegister(List<ClientServiceDescription> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);
        RegistrationMessage registrationMessage = new RegistrationMessage(RegistrationMessage.TYPE_REGISTER);
        registrationMessage.setChannelId(ClientStart.CLIENT_ID);
        registrationMessage.setAuth(clientConfig.getSecrete());
        registrationMessage.setTcpServiceDescriptions(list.stream()
                .map(ClientServiceDescription::toTcpServiceDescription)
                .collect(Collectors.toList()));

        try {
            sendRegistrationMessage(registrationMessage, NDCMessageProtocol.SERVICE_REGISTER);
        } catch (UnknownHostException e) {
            log.error(e + "");
        }
    }

    private ClientServiceDescription toClientServiceDescription(TcpServiceDescription tcpServiceDescription) {
        ClientServiceDescription clientServiceDescription = new ClientServiceDescription();
        clientServiceDescription.setId(tcpServiceDescription.getId());
        clientServiceDescription.setServiceName(tcpServiceDescription.getServiceName());
        clientServiceDescription.setServiceIp(tcpServiceDescription.getServiceIp());
        clientServiceDescription.setServicePort(tcpServiceDescription.getServicePort());
        clientServiceDescription.setDescription(tcpServiceDescription.getDescription());
        clientServiceDescription.setServiceEnable(true);
        clientServiceDescription.performParameterVerification();
        return clientServiceDescription;
    }

    private String buildServiceKey(ClientServiceDescription serviceDescription) {
        return serviceDescription.getServiceIp() + ":" + serviceDescription.getServicePort();
    }

    private boolean isServiceChanged(ClientServiceDescription currentService, ClientServiceDescription targetService) {
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

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) throws Exception {
        byte type = ndcMessageProtocol.getType();

        try {
            switch (type) {
                case NDCMessageProtocol.CHANNEL_HEART_BEAT:
                    log.debug("get heart beat from server");
                    break;

                case NDCMessageProtocol.TCP_DATA:
                    log.debug("get tcp data from server: " + ndcMessageProtocol);
                    UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToReceiveQueue(ndcMessageProtocol);
                    break;

                case NDCMessageProtocol.TCP_ACTIVE:
                    log.debug("get active from server: " + ndcMessageProtocol);
                    UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToReceiveQueue(ndcMessageProtocol);
                    break;

                case NDCMessageProtocol.OPEN_CHANNEL:
                    handleOpenChannelResponse(channelHandlerContext, ndcMessageProtocol);
                    break;

                case NDCMessageProtocol.SERVICE_REGISTER:
                    log.warn("unexpected register message from server");
                    break;

                case NDCMessageProtocol.SERVICE_UNREGISTER:
                    log.info("unregister success");
                    break;

                case NDCMessageProtocol.SERVICE_CONTROL_SYNC:
                    handleServiceControlSync(ndcMessageProtocol);
                    break;

                case NDCMessageProtocol.CONNECTION_INTERRUPTED:
                    log.debug("interrupt connection " + ndcMessageProtocol);
                    UniqueBeanManage.getBean(JNDCClientConfigCenter.class).shutDownClientServiceProvider(ndcMessageProtocol);
                    break;

                case NDCMessageProtocol.NO_ACCESS:
                    reConnectTag = false;
                    channelHandlerContext.close();
                    log.error("连接密码错误，关闭连接");
                    break;

                case NDCMessageProtocol.USER_ERROR:
                    UserError userError = ndcMessageProtocol.getObject(UserError.class);
                    if (userError.getDescription() != null && userError.getDescription().startsWith("Ip Address Rule")) {
                        log.error(userError.getDescription());
                        reConnectTag = false;
                        channelHandlerContext.close();
                        break;
                    }
                    log.error(userError.toString());
                    break;

                case NDCMessageProtocol.UN_CATCHABLE_ERROR:
                    log.error(new String(ndcMessageProtocol.getData()));
                    break;

                default:
                    log.warn("收到未知消息类型: 0x" + Integer.toHexString(type & 0xFF) + "，拒绝处理");
                    break;
            }
        } catch (Exception e) {
            NDCMessageProtocol copy = ndcMessageProtocol.copy();
            copy.setType(NDCMessageProtocol.CONNECTION_INTERRUPTED);
            copy.setData(NDCMessageProtocol.BLANK);
            UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);
            e.printStackTrace();
            log.error(type + ": client get a unCatchable Error:" + e);
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (reConnectTag) {
            log.debug("client connection interrupted, will restart on 5 second later");
            // TimeUnit.SECONDS.sleep(5);
            client.tryReconnect();
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        if (cause instanceof DecoderException && cause.getCause() instanceof SecreteDecodeFailException) {
            // 密钥错误，不可恢复
            JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);
            log.error("密钥\"" + clientConfig.getSecrete() + "\"错误，请与密钥提供者确认,程序即将退出...");
            ApplicationExit.exit();
            return;
        }

        // 其他异常：关闭连接，触发重连
        channelHandlerContext.close();
        log.error("unCatchable client error：" + cause.getMessage());
    }

}
