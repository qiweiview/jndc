package jndc.server;

import io.netty.channel.*;
import jndc.core.*;
import jndc.core.config.UnifiedConfiguration;
import jndc.core.data_store.DBWrapper;
import jndc.core.data_store.DataStore;
import jndc.core.message.RegistrationMessage;
import jndc.core.message.UserError;
import jndc.exception.SecreteDecodeFailException;
import jndc.utils.LogPrint;
import jndc.utils.ObjectSerializableUtils;
import jndc.utils.ThreadQueue;
import jndc.utils.UUIDSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.core.MessageNotificationCenter;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JNDCServerMessageHandle extends SimpleChannelInboundHandler<NDCMessageProtocol> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String NAME = "NDC_SERVER_HANDLE";


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) throws Exception {
        byte type = ndcMessageProtocol.getType();


        try {
            if (type == NDCMessageProtocol.CHANNEL_HEART_BEAT) {
                //todo CHANNEL_HEART_BEAT
                //just accept
                logger.debug("get heart beat");

            }

            if (type == NDCMessageProtocol.TCP_DATA) {
                //todo TCP_DATA
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.addMessageToReceiveQueue(ndcMessageProtocol);

            }

            if (type == NDCMessageProtocol.MAP_REGISTER) {
                //todo MAP_REGISTER

                //copy message
                NDCMessageProtocol copy = ndcMessageProtocol.copy();

                UnifiedConfiguration unifiedConfiguration = UniqueBeanManage.getBean(UnifiedConfiguration.class);
                String secrete = unifiedConfiguration.getSecrete();


                RegistrationMessage registrationMessage = ndcMessageProtocol.getObject(RegistrationMessage.class);
                String auth = registrationMessage.getAuth();

                if (auth == null || !secrete.equals(auth)) {
                    //todo auth fail
                    copy.setType(NDCMessageProtocol.NO_ACCESS);
                    UserError userError = new UserError();
                    byte[] bytes = ObjectSerializableUtils.object2bytes(userError);
                    copy.setData(bytes);
                    channelHandlerContext.writeAndFlush(copy);
                    logger.error("auth fail with:" + auth);
                    return;
                }


                //registerServiceProvider
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ChannelHandlerContextHolder channelHandlerContextHolder = new ChannelHandlerContextHolder();
                channelHandlerContextHolder.setChannelHandlerContext(channelHandlerContext);
                List<TcpServiceDescription> tcpServiceDescriptions = registrationMessage.getTcpServiceDescriptions();
                channelHandlerContextHolder.setTcpServiceDescriptions(tcpServiceDescriptions);



                /* -------------------restore the bind relation------------------- */

                //put new register service into map
                Map<String, TcpServiceDescription> map = new HashMap<>();
                tcpServiceDescriptions.forEach(x -> {
                    map.put(x.getRouteTo(), x);
                });

                //find the old "port service bind"
                DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
                List<ServerPortBind> serverPortBinds = dbWrapper.listAll();

                //find match "port service bind"
                serverPortBinds.forEach(x -> {
                    String routeTo = x.getRouteTo();
                    TcpServiceDescription tcpServiceDescription = map.get(routeTo);
                    if (tcpServiceDescription != null) {
                        //todo do rebind

                        //rebind the port service
                        boolean success = ndcServerConfigCenter.addTCPRouter(x.getPort(), tcpServiceDescription);

                        if (success) {
                            x.setPortEnable(1);
                            logger.debug("rebind the service:" + routeTo+" success");
                        } else {
                            x.setPortEnable(0);
                            logger.error("rebind the service:" + routeTo+" fail");
                        }

                        dbWrapper.updateByPrimaryKey(x);

                    }
                });


                //do register
                ndcServerConfigCenter.registerServiceProvider(channelHandlerContextHolder);


                // send response
                RegistrationMessage response = new RegistrationMessage();
                response.setMessage("service has been successfully registered(msg from server)");
                byte[] bytes = ObjectSerializableUtils.object2bytes(response);
                copy.setData(bytes);
                channelHandlerContext.writeAndFlush(copy);


                MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
                messageNotificationCenter.dateRefreshMessage("channelList");//notice the channel list refresh
                messageNotificationCenter.dateRefreshMessage("serviceList");//notice the service list refresh
                messageNotificationCenter.dateRefreshMessage("serverPortList");//notice the server port list refresh
            }

            if (type == NDCMessageProtocol.CONNECTION_INTERRUPTED) {
                //todo CONNECTION_INTERRUPTED
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.connectionInterrupt(ndcMessageProtocol);
            }

            if (type == NDCMessageProtocol.NO_ACCESS) {
                //todo NO_ACCESS
                logger.debug(new String(ndcMessageProtocol.getData()));
            }

            if (type == NDCMessageProtocol.USER_ERROR) {
                //todo USER_ERROR
                logger.error(new String(ndcMessageProtocol.getData()));

            }

            if (type == NDCMessageProtocol.UN_CATCHABLE_ERROR) {
                //todo UN_CATCHABLE_ERROR
                logger.error(new String(ndcMessageProtocol.getData()));
            }

        } catch (Exception e) {
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

        logger.error("unCatchable server error：" + cause.getMessage());

    }


}
