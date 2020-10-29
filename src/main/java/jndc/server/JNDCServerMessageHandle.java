package jndc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.message.RegistrationMessage;
import jndc.utils.LogPrint;
import jndc.utils.ObjectSerializableUtils;

import java.net.SocketException;

public class JNDCServerMessageHandle extends SimpleChannelInboundHandler<NDCMessageProtocol> {

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
                RegistrationMessage registrationMessage = new RegistrationMessage();
                registrationMessage.setMessage(ndcMessageProtocol.getServerPort() + " on server register success");
                byte[] bytes = ObjectSerializableUtils.object2bytes(registrationMessage);
                copy.setData(bytes);
                channelHandlerContext.writeAndFlush(copy);


                //register message channel
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.registerMessageChannel(channelHandlerContext);


                //start port monitor
                ndcServerConfigCenter.startPortMonitoring(copy);
            }

            if (type == NDCMessageProtocol.CONNECTION_INTERRUPTED) {
                //todo CONNECTION_INTERRUPTED
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.shutDownTcpConnection(ndcMessageProtocol);
            }

            if (type == NDCMessageProtocol.NO_ACCESS) {
                //todo CONNECTION_INTERRUPTED
                LogPrint.log(new String(ndcMessageProtocol.getData()));
            }

            if (type == NDCMessageProtocol.USER_ERROR) {
                //todo UN_CATCHABLE_ERROR
                LogPrint.log(new String(ndcMessageProtocol.getData()));

            }

            if (type == NDCMessageProtocol.UN_CATCHABLE_ERROR) {
                //todo UN_CATCHABLE_ERROR
                LogPrint.log(new String(ndcMessageProtocol.getData()));
            }

        } catch (Exception e) {
            ndcMessageProtocol.setType(NDCMessageProtocol.USER_ERROR);
            ndcMessageProtocol.inetSwap();
            ndcMessageProtocol.setData("error".getBytes());
            channelHandlerContext.writeAndFlush(e);
        }


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogPrint.log("connection closed");
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof SocketException) {
            LogPrint.log("client close：" + cause.getMessage());
        }

    }
}
