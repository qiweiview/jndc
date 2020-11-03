package jndc.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.config.ClientConfig;
import jndc.core.config.UnifiedConfiguration;
import jndc.core.message.RegistrationMessage;
import jndc.core.message.UserError;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;
import jndc.utils.ObjectSerializableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class JNDCServerMessageHandle extends SimpleChannelInboundHandler<NDCMessageProtocol> {
    private  final Logger logger = LoggerFactory.getLogger(getClass());
    
    public static final String NAME = "NDC_SERVER_HANDLE";


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) throws Exception {
        Integer type = ndcMessageProtocol.getType();

        try {


            if (type == NDCMessageProtocol.TCP_DATA) {
                //todo TCP_DATA
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.addMessageToReceiveQueue(ndcMessageProtocol);
            }

            if (type == NDCMessageProtocol.MAP_REGISTER) {
                //todo MAP_REGISTER
                NDCMessageProtocol copy = ndcMessageProtocol.copy();

                UnifiedConfiguration unifiedConfiguration = UniqueBeanManage.getBean(UnifiedConfiguration.class);
                String secrete = unifiedConfiguration.getSecrete();


                RegistrationMessage object = ndcMessageProtocol.getObject(RegistrationMessage.class);
                String auth = object.getAuth();

                if (auth == null || !secrete.equals(auth)) {
                    //todo auth fail
                    copy.setType(NDCMessageProtocol.USER_ERROR);
                    UserError userError = new UserError();
                    userError.setCode(UserError.AUTH_ERROR);
                    byte[] bytes = ObjectSerializableUtils.object2bytes(userError);
                    copy.setData(bytes);
                    channelHandlerContext.writeAndFlush(copy);
                    logger.debug("auth fail with:"+auth);
                    return;
                }


                //register message channel,just focus on port is used or not
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.registerMessageChannel(copy.getServerPort(), channelHandlerContext);

                //start port monitor
                ndcServerConfigCenter.startPortMonitoring(copy);

                // send response
                RegistrationMessage registrationMessage = new RegistrationMessage();
                registrationMessage.setMessage(ndcMessageProtocol.getServerPort() + " on server register success");
                byte[] bytes = ObjectSerializableUtils.object2bytes(registrationMessage);
                copy.setData(bytes);
                channelHandlerContext.writeAndFlush(copy);

            }

            if (type == NDCMessageProtocol.CONNECTION_INTERRUPTED) {
                //todo CONNECTION_INTERRUPTED
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.shutDownTcpConnection(ndcMessageProtocol);
            }

            if (type == NDCMessageProtocol.NO_ACCESS) {
                //todo NO_ACCESS
                logger.debug(new String(ndcMessageProtocol.getData()));
            }

            if (type == NDCMessageProtocol.USER_ERROR) {
                //todo USER_ERROR
                logger.debug(new String(ndcMessageProtocol.getData()));

            }

            if (type == NDCMessageProtocol.UN_CATCHABLE_ERROR) {
                //todo UN_CATCHABLE_ERROR
                logger.debug(new String(ndcMessageProtocol.getData()));
            }

        } catch (Exception e) {

            logger.debug("unCatchableError:"+e);
            ndcMessageProtocol.setType(NDCMessageProtocol.USER_ERROR);
            UserError userError = new UserError();
            userError.setCode(UserError.SERVER_ERROR);
            byte[] bytes = ObjectSerializableUtils.object2bytes(userError);
            ndcMessageProtocol.setData(bytes);
            channelHandlerContext.writeAndFlush(e);
        }


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        InetAddress address = socketAddress.getAddress();
        logger.debug("client connection closed：" + address);
        ctx.close();
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        ndcServerConfigCenter.unRegisterMessageChannel(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("unCatchable server error：" + cause.getMessage());


        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();

        //for the client local is remote
        NDCMessageProtocol of = NDCMessageProtocol.of(localAddress.getAddress(), remoteAddress.getAddress(), 0, localAddress.getPort(), remoteAddress.getPort(), NDCMessageProtocol.UN_CATCHABLE_ERROR);
        of.setData(cause.toString().getBytes());
        ctx.writeAndFlush(of).addListeners(ChannelFutureListener.CLOSE);

    }


}
