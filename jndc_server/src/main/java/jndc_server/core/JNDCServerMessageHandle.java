package jndc_server.core;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.SimpleChannelInboundHandler;
import jndc.core.NDCMessageProtocol;
import jndc.core.TcpServiceDescription;

import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.message.OpenChannelMessage;
import jndc.core.message.RegistrationMessage;
import jndc.core.message.UserError;
import jndc.exception.SecreteDecodeFailException;
import jndc.utils.ObjectSerializableUtils;
import jndc.utils.UUIDSimple;
import jndc_server.databases_object.ChannelContextCloseRecord;
import jndc_server.databases_object.ServerPortBind;
import jndc_server.web_support.core.MessageNotificationCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JNDCServerMessageHandle extends SimpleChannelInboundHandler<NDCMessageProtocol> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final Logger logger_static = LoggerFactory.getLogger(JNDCServerMessageHandle.class);

    public static final String NAME = "NDC_SERVER_HANDLE";


    /**
     * do rebind operation
     * @param tcpServiceDescriptionOnServers
     */
    public static void serviceRebind(List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers) {
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        //put new register service into map
        Map<String, TcpServiceDescriptionOnServer> map = new HashMap<>();
        tcpServiceDescriptionOnServers.forEach(x -> {
            map.put(x.getBindClientId(), x);
        });

        //find the old "port service bind"
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        List<ServerPortBind> serverPortBinds = dbWrapper.customQuery("select * from server_port_bind where portEnable=0 and bindClientId is not null  ");

        //find match "port service bind"
        serverPortBinds.forEach(x -> {
            String bindClientId = x.getBindClientId();
            String routeTo = x.getRouteTo();

            TcpServiceDescriptionOnServer tcpServiceDescription = map.get(bindClientId);
            logger_static.info("rebind:"+map+"----->"+bindClientId);
            if (tcpServiceDescription != null) {
                //todo do rebind



                //rebind the port service
                boolean success = ndcServerConfigCenter.addTCPRouter(x.getPort(),x.getEnableDateRange(), tcpServiceDescription);

                if (success) {
                    x.bindEnable();
                    logger_static.info("rebind the service:" +routeTo+ " success");
                } else {
                    x.bindDisable();
                    logger_static.error("rebind the service:" + routeTo + " fail");
                }

                dbWrapper.updateByPrimaryKey(x);

            }
        });
    }

    private NDCMessageProtocol authCheck(NDCMessageProtocol copy, String tokenFromRequest) {
        JNDCServerConfig jndcServerConfig = UniqueBeanManage.getBean(JNDCServerConfig.class);
        String secrete = jndcServerConfig.getSecrete();

        if (tokenFromRequest == null || !secrete.equals(tokenFromRequest)) {
            //todo auth fail
            copy.setType(NDCMessageProtocol.NO_ACCESS);
            UserError userError = new UserError();
            byte[] bytes = ObjectSerializableUtils.object2bytes(userError);
            copy.setData(bytes);
            logger.error("auth fail with:" + tokenFromRequest);
            return copy;
        }
        return null;
    }


    private void handleUnRegisterService(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) {
        //copy message
        NDCMessageProtocol copy = ndcMessageProtocol.copy();

        //===================auth check===========================
        RegistrationMessage registrationMessage = ndcMessageProtocol.getObject(RegistrationMessage.class);
        String auth = registrationMessage.getAuth();
        NDCMessageProtocol checkResult = authCheck(copy, auth);
        if (checkResult != null) {
            channelHandlerContext.writeAndFlush(copy);
            return;
        }
        //========================================================

        //registerServiceProvider
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        List<TcpServiceDescription> tcpServiceDescriptions = registrationMessage.getTcpServiceDescriptions();
        List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers = TcpServiceDescriptionOnServer.ofArray(tcpServiceDescriptions);

        //do register
        ndcServerConfigCenter.removeServiceByChannelId(registrationMessage.getChannelId(), tcpServiceDescriptionOnServers);
    }

    /**
     * handle the register service message
     *
     * @param channelHandlerContext
     * @param ndcMessageProtocol
     */
    private void handleRegisterService(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) {
        //copy message
        NDCMessageProtocol copy = ndcMessageProtocol.copy();


        //===================auth check===========================
        RegistrationMessage registrationMessage = ndcMessageProtocol.getObject(RegistrationMessage.class);
        String auth = registrationMessage.getAuth();
        NDCMessageProtocol checkResult = authCheck(copy, auth);
        if (checkResult != null) {
            channelHandlerContext.writeAndFlush(copy);
            return;
        }
        //========================================================


        //registerServiceProvider
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);


        String channelId = registrationMessage.getChannelId();


        List<TcpServiceDescription> tcpServiceDescriptions = registrationMessage.getTcpServiceDescriptions();
        List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers = TcpServiceDescriptionOnServer.ofArray(tcpServiceDescriptions);

        //set current bound client
        tcpServiceDescriptionOnServers.forEach(x->{
            x.setBindClientId(channelId);
        });

        //do register
        ndcServerConfigCenter.addServiceByChannelId(registrationMessage.getChannelId(), tcpServiceDescriptionOnServers);

        /* ============================= restore the bind relation ============================= */
        serviceRebind(tcpServiceDescriptionOnServers);


        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
        messageNotificationCenter.dateRefreshMessage("serviceList");//notice the service list refresh
        messageNotificationCenter.dateRefreshMessage("serverPortList");//notice the server port list refresh
    }

    /**
     * handle the open channel message
     *
     * @param channelHandlerContext
     * @param ndcMessageProtocol
     */
    private void handleOpenChannel(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) {
        //copy message
        NDCMessageProtocol copy = ndcMessageProtocol.copy();


        //===================auth check===========================
        OpenChannelMessage openChannelMessage = ndcMessageProtocol.getObject(OpenChannelMessage.class);
        String auth = openChannelMessage.getAuth();
        NDCMessageProtocol checkResult = authCheck(copy, auth);
        if (checkResult != null) {
            //todo auth check fail
            channelHandlerContext.writeAndFlush(copy);
            return;
        }
        //========================================================


        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        ChannelHandlerContextHolder channelHandlerContextHolder = new ChannelHandlerContextHolder();
        channelHandlerContextHolder.setChannelHandlerContext(channelHandlerContext);
        ndcServerConfigCenter.registerServiceProvider(channelHandlerContextHolder);


        // send response
        copy.setType(NDCMessageProtocol.OPEN_CHANNEL);
        OpenChannelMessage response = new OpenChannelMessage();
        response.setChannelId(channelHandlerContextHolder.getId());
        byte[] bytes = ObjectSerializableUtils.object2bytes(response);
        copy.setData(bytes);
        channelHandlerContext.writeAndFlush(copy);

        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
        messageNotificationCenter.dateRefreshMessage("channelList");//notice the channel list refresh
    }
    private void handleHeartBeatFromClient(OpenChannelMessage registrationMessage) {
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        ndcServerConfigCenter.refreshHeartBeatTimeStamp(registrationMessage.getChannelId());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) throws Exception {
        byte type = ndcMessageProtocol.getType();


        try {
            /*==================================== CHANNEL_HEART_BEAT ====================================*/
            if (type == NDCMessageProtocol.CHANNEL_HEART_BEAT) {
                //todo CHANNEL_HEART_BEAT
                //just accept
                logger.debug("get heart beat");

                OpenChannelMessage registrationMessage = ndcMessageProtocol.getObject(OpenChannelMessage.class);
                handleHeartBeatFromClient(registrationMessage);

            }

            /*==================================== TCP_DATA ====================================*/
            if (type == NDCMessageProtocol.TCP_DATA) {
                //todo TCP_DATA
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.addMessageToReceiveQueue(ndcMessageProtocol);
            }

            /*==================================== OPEN_CHANNEL ====================================*/
            if (type == NDCMessageProtocol.OPEN_CHANNEL) {
                //todo OPEN_CHANNEL
                handleOpenChannel(channelHandlerContext, ndcMessageProtocol);
            }

            /*==================================== SERVICE_REGISTER ====================================*/
            if (type == NDCMessageProtocol.SERVICE_REGISTER) {
                //todo SERVICE_REGISTER
                handleRegisterService(channelHandlerContext, ndcMessageProtocol);
            }


            /*==================================== SERVICE_UNREGISTER ====================================*/
            if (type == NDCMessageProtocol.SERVICE_UNREGISTER) {
                //todo SERVICE_UNREGISTER
                handleUnRegisterService(channelHandlerContext, ndcMessageProtocol);
                MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
                messageNotificationCenter.dateRefreshMessage("serviceList");//notice the service list refresh
            }

            /*==================================== CONNECTION_INTERRUPTED ====================================*/
            if (type == NDCMessageProtocol.CONNECTION_INTERRUPTED) {
                //todo CONNECTION_INTERRUPTED
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.connectionInterrupt(ndcMessageProtocol);

                MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
                messageNotificationCenter.dateRefreshMessage("serviceList");//notice the service list refresh
                messageNotificationCenter.dateRefreshMessage("serverPortList");//notice the server port list refresh
            }

            /*==================================== NO_ACCESS ====================================*/
            if (type == NDCMessageProtocol.NO_ACCESS) {
                //todo NO_ACCESS
                logger.debug(new String(ndcMessageProtocol.getData()));
            }

            /*==================================== USER_ERROR ====================================*/
            if (type == NDCMessageProtocol.USER_ERROR) {
                //todo USER_ERROR
                logger.error(new String(ndcMessageProtocol.getData()));

            }

            /*==================================== UN_CATCHABLE_ERROR ====================================*/
            if (type == NDCMessageProtocol.UN_CATCHABLE_ERROR) {
                //todo UN_CATCHABLE_ERROR
                logger.error(new String(ndcMessageProtocol.getData()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("unCatchableError:" + e);
            ndcMessageProtocol.setType(NDCMessageProtocol.USER_ERROR);
            UserError userError = new UserError();
            userError.setCode(UserError.SERVER_ERROR);
            byte[] bytes = ObjectSerializableUtils.object2bytes(userError);
            ndcMessageProtocol.setData(bytes);
            channelHandlerContext.writeAndFlush(ndcMessageProtocol);
        }


    }




    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        //unregister  service provider
        ChannelContextCloseRecord channelContextCloseRecord = ndcServerConfigCenter.unRegisterServiceProvider(ctx);


        if (channelContextCloseRecord != null) {
            //record channel interrupt record
            AsynchronousEventCenter asynchronousEventCenter = UniqueBeanManage.getBean(AsynchronousEventCenter.class);
            asynchronousEventCenter.dbJob(() -> {
                channelContextCloseRecord.setId(UUIDSimple.id());
                channelContextCloseRecord.setTimeStamp(System.currentTimeMillis());
                DBWrapper<ChannelContextCloseRecord> dbWrapper = DBWrapper.getDBWrapper(channelContextCloseRecord);
                dbWrapper.insert(channelContextCloseRecord);
            });
        }

        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
        messageNotificationCenter.dateRefreshMessage("channelList");//notice the channel list refresh
        messageNotificationCenter.dateRefreshMessage("serviceList");//notice the service list refresh
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();


        if (cause.getCause() instanceof SecreteDecodeFailException) {
            NDCMessageProtocol of = NDCMessageProtocol.of(localAddress.getAddress(), remoteAddress.getAddress(), 0, localAddress.getPort(), remoteAddress.getPort(), NDCMessageProtocol.NO_ACCESS);
            ctx.writeAndFlush(of).addListeners(ChannelFutureListener.CLOSE);
            logger.error("The \"" + remoteAddress + "\" is broken due to incorrect credentials");
            return;
        }


        //for the client local is remote
        NDCMessageProtocol of = NDCMessageProtocol.of(localAddress.getAddress(), remoteAddress.getAddress(), 0, localAddress.getPort(), remoteAddress.getPort(), NDCMessageProtocol.UN_CATCHABLE_ERROR);
        of.setData(cause.toString().getBytes());
        ctx.writeAndFlush(of).addListeners(ChannelFutureListener.CLOSE);

        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
        }
        logger.error("unCatchable server error：" + cause);

    }


}
