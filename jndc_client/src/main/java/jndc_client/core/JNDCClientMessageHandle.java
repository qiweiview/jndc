package jndc_client.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import jndc.core.NDCMessageProtocol;
import jndc.core.TcpServiceDescription;
import jndc.core.UniqueBeanManage;
import jndc.core.message.OpenChannelMessage;
import jndc.core.message.RegistrationMessage;
import jndc.core.message.UserError;
import jndc.exception.SecreteDecodeFailException;
import jndc.utils.ObjectSerializableUtils;
import jndc_client.http_support.ClientHttpManagement;
import jndc_client.start.ClientStart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JNDCClientMessageHandle extends SimpleChannelInboundHandler<NDCMessageProtocol> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
            logger.error("ignore empty list");
            return;
        }
        stopRegister(Stream.of(list).collect(Collectors.toList()));
    }

    public void startRegister(ClientServiceDescription... list) {
        if (list == null || list.length < 1) {
            logger.error("ignore empty list");
            return;
        }
        startRegister(Stream.of(list).collect(Collectors.toList()));
    }

    public void stopRegister(List<ClientServiceDescription> list) {

        JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);
        JNDCClientConfigCenter jndcClientConfigCenter = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);

        if (list == null || list.size() < 1) {
            logger.error("ignore empty list");
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
            logger.error(e + "");
        }
    }

    public void startRegister(List<ClientServiceDescription> list) {
        JNDCClientConfig clientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);
        JNDCClientConfigCenter jndcClientConfigCenter = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);

        if (list == null || list.size() < 1) {
            logger.error("ignore empty list");
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
                logger.info("ignore the mapping:" + x.getServiceName());
            }
        });

        if (tcpServiceDescriptions.size() < 1) {
            logger.info("do not send register message ,because  there is not any enable service  on config file ");
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
            logger.error(e + "");
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
            logger.error("can not load service support config");
            return;
        }

        //get service list by config file
        List<ClientServiceDescription> clientServiceDescriptions = clientConfig.getClientServiceDescriptions();
        //添加管理页面到注册服务
        clientServiceDescriptions.add(ClientHttpManagement.DEPLOY_PORT);
        startRegister(clientServiceDescriptions);
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
                logger.info("get heart beat from server");

            }

            if (type == NDCMessageProtocol.TCP_DATA) {
                //todo TCP_DATA
                logger.debug("get tcp data from server: " + ndcMessageProtocol);
                JNDCClientConfigCenter bean = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
                bean.addMessageToReceiveQueue(ndcMessageProtocol);
                return;
            }


            if (type == NDCMessageProtocol.TCP_ACTIVE) {
                //todo TCP_ACTIVE
                logger.debug("get active from server: " + ndcMessageProtocol);
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

                logger.info("not expect get  a register message from server");

            }

            if (type == NDCMessageProtocol.SERVICE_UNREGISTER) {
                //todo SERVICE_UNREGISTER
                logger.info("unregister success");
            }

            /* ================================== CONNECTION_INTERRUPTED ================================== */
            if (type == NDCMessageProtocol.CONNECTION_INTERRUPTED) {
                //todo CONNECTION_INTERRUPTED 连接由服务端中断
                logger.info("interrupt  connection " + ndcMessageProtocol);
                JNDCClientConfigCenter bean = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
                bean.shutDownClientServiceProvider(ndcMessageProtocol);
                return;
            }

            if (type == NDCMessageProtocol.NO_ACCESS) {
                //todo NO_ACCESS
                reConnectTag = false;//not restart
                channelHandlerContext.close();
                channelHandlerContext.channel().eventLoop().shutdownGracefully().addListener(x -> {
                    if (x.isSuccess()) {
                        logger.error("register auth fail, the client will close later...");
                    } else {
                        logger.error("shutdown fail");
                    }
                });
                return;
            }

            if (type == NDCMessageProtocol.USER_ERROR) {
                //todo USER_ERROR
                UserError userError = ndcMessageProtocol.getObject(UserError.class);
                logger.error(userError.toString());
                return;
            }

            if (type == NDCMessageProtocol.UN_CATCHABLE_ERROR) {
                //todo UN_CATCHABLE_ERROR
                logger.error(new String(ndcMessageProtocol.getData()));
                return;
            }

        } catch (Exception e) {
            NDCMessageProtocol copy = ndcMessageProtocol.copy();
            copy.setType(NDCMessageProtocol.CONNECTION_INTERRUPTED);
            copy.setData(NDCMessageProtocol.BLANK);
            UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);

            logger.error(type + ": client get a unCatchable Error:" + e);
        }


    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (reConnectTag) {
            logger.info("client connection interrupted, will restart on 5 second later");
            // TimeUnit.SECONDS.sleep(5);
            client.tryReconnect();
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        if (cause instanceof DecoderException) {
            if (cause.getCause() instanceof SecreteDecodeFailException) {
                //auth fail
                logger.error("secrete check error when decode,please check the secrete later...");
                cause.printStackTrace();
                //ApplicationExit.exit();

            }
            channelHandlerContext.close();
            logger.error("unCatchable client error：" + cause.getMessage());
        }
    }

}
