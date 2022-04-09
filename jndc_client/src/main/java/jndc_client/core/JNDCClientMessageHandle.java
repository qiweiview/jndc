package jndc_client.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.message.OpenChannelMessage;
import jndc.core.message.RegistrationMessage;
import jndc.core.message.TcpServiceDescription;
import jndc.core.message.UserError;
import jndc.exception.SecreteDecodeFailException;
import jndc.utils.ObjectSerializableUtils;
import jndc_client.http_support.ClientHttpManagement;
import jndc_client.start.ClientStart;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JNDCClientMessageHandle extends SimpleChannelInboundHandler<NDCMessageProtocol> {

    private JNDCClient client;

    public static final String NAME = "NDC_CLIENT_HANDLE";

    private ChannelHandlerContext ctx;

    private volatile boolean reConnectTag = true;

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
        byte[] bytes = ObjectSerializableUtils.object2bytes(registrationMessage);


        try {
            InetAddress unused = InetAddress.getLocalHost();
            NDCMessageProtocol tqs = NDCMessageProtocol.of(unused, unused, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.SERVICE_UNREGISTER);
            tqs.setData(bytes);

            //send data
            ctx.writeAndFlush(tqs);
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
                jndcClientConfigCenter.initService(x);//init the support service

            } else {
                log.info("ignore the mapping:" + x.getServiceName());
            }
        });

        if (tcpServiceDescriptions.size() < 1) {
            log.info("do not send register message ,because  there is not any enable service  on config file ");
            return;
        }

        //create register message
        RegistrationMessage registrationMessage = new RegistrationMessage(RegistrationMessage.TYPE_REGISTER);
        registrationMessage.setChannelId(ClientStart.CLIENT_ID);
        registrationMessage.setAuth(clientConfig.getSecrete());


        registrationMessage.setTcpServiceDescriptions(tcpServiceDescriptions);
        byte[] bytes = ObjectSerializableUtils.object2bytes(registrationMessage);


        try {
            InetAddress unused = InetAddress.getLocalHost();
            NDCMessageProtocol tqs = NDCMessageProtocol.of(unused, unused, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.SERVICE_REGISTER);
            tqs.setData(bytes);

            //send data
            ctx.writeAndFlush(tqs);
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

        final boolean[] addPage = {true};
        clientServiceDescriptions.forEach(x -> {
            if (ClientHttpManagement.CLIENT_MANAGEMENT.equals(x.getServiceName())) {
                addPage[0] = false;
            }
        });
        //添加管理页面到注册服务
        if (addPage[0]) {
            clientServiceDescriptions.add(ClientHttpManagement.DEPLOY_PORT);
        }
        startRegister(clientServiceDescriptions);
        log.info("注册完成...");
    }

    private void handleOpenChannelResponse(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) throws Exception {
        OpenChannelMessage object = ndcMessageProtocol.getObject(OpenChannelMessage.class);
        JNDCClientConfigCenter jndcClientConfigCenter = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
        jndcClientConfigCenter.registerMessageChannel(channelHandlerContext);
        InetAddress unused = InetAddress.getLocalHost();

        //use the message with id
        final NDCMessageProtocol tqs = NDCMessageProtocol.of(unused, unused, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.CHANNEL_HEART_BEAT);
        byte[] bytes = ObjectSerializableUtils.object2bytes(object);


        //use the message from server as  heartbeat request message
        EventLoop eventExecutors = channelHandlerContext.channel().eventLoop();
        eventExecutors.scheduleWithFixedDelay(() -> {
            //todo 心跳
            tqs.setData(bytes);//necessary
            jndcClientConfigCenter.addMessageToSendQueue(tqs);
        }, 0, 60, TimeUnit.SECONDS);

        log.info("准备启动服务注册...");
        //send register message
        sendServiceRegisterMessage();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) throws Exception {
        byte type = ndcMessageProtocol.getType();

        try {


            if (type == NDCMessageProtocol.CHANNEL_HEART_BEAT) {
                //todo CHANNEL_HEART_BEAT
                //just accept
                log.info("get heart beat from server");

            }

            if (type == NDCMessageProtocol.TCP_DATA) {
                //todo TCP_DATA
                log.debug("get tcp data from server: " + ndcMessageProtocol);
                JNDCClientConfigCenter bean = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
                bean.addMessageToReceiveQueue(ndcMessageProtocol);
                return;
            }


            if (type == NDCMessageProtocol.TCP_ACTIVE) {
                //todo TCP_ACTIVE
                log.debug("get active from server: " + ndcMessageProtocol);
                JNDCClientConfigCenter bean = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
                bean.addMessageToReceiveQueue(ndcMessageProtocol);
                return;
            }


            if (type == NDCMessageProtocol.OPEN_CHANNEL) {
                //todo OPEN_CHANNEL
                handleOpenChannelResponse(channelHandlerContext, ndcMessageProtocol);
            }


            if (type == NDCMessageProtocol.SERVICE_REGISTER) {
                //todo SERVICE_REGISTER

                log.info("not expect get  a register message from server");

            }

            if (type == NDCMessageProtocol.SERVICE_UNREGISTER) {
                //todo SERVICE_UNREGISTER
                log.info("unregister success");
            }

            /* ================================== CONNECTION_INTERRUPTED ================================== */
            if (type == NDCMessageProtocol.CONNECTION_INTERRUPTED) {
                //todo CONNECTION_INTERRUPTED 连接由服务端中断
                log.debug("interrupt  connection " + ndcMessageProtocol);
                JNDCClientConfigCenter bean = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
                bean.shutDownClientServiceProvider(ndcMessageProtocol);
                return;
            }

            if (type == NDCMessageProtocol.NO_ACCESS) {
                //todo NO_ACCESS
                reConnectTag = false;//not restart
                channelHandlerContext.close();
                log.error("连接密码错误...");
                System.exit(1);
//                channelHandlerContext.channel().eventLoop().shutdownGracefully().addListener(x -> {
//                    if (x.isSuccess()) {
//                        log.error("register auth fail, the client will close later...");
//                    } else {
//                        log.error("shutdown fail");
//                    }
//                });
//                return;
            }

            if (type == NDCMessageProtocol.USER_ERROR) {
                //todo USER_ERROR
                UserError userError = ndcMessageProtocol.getObject(UserError.class);
                if (userError.getDescription().startsWith("Ip Address Rule")) {
                    log.error(userError.getDescription());
                    System.exit(1);
                }
                log.error(userError.toString());
                return;
            }

            if (type == NDCMessageProtocol.UN_CATCHABLE_ERROR) {
                //todo UN_CATCHABLE_ERROR
                log.error(new String(ndcMessageProtocol.getData()));
                return;
            }

        } catch (Exception e) {
            NDCMessageProtocol copy = ndcMessageProtocol.copy();
            copy.setType(NDCMessageProtocol.CONNECTION_INTERRUPTED);
            copy.setData(NDCMessageProtocol.BLANK);
            UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);

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
        if (cause instanceof DecoderException) {
            if (cause.getCause() instanceof SecreteDecodeFailException) {
                //auth fail
                log.error("secrete check error when decode,please check the secrete later...");
                cause.printStackTrace();
                //ApplicationExit.exit();

            }
            channelHandlerContext.close();
            log.error("unCatchable client error：" + cause.getMessage());
        }
    }

}
