package jndc.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.message.RegistrationMessage;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;
import jndc.utils.ObjectSerializableUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
                LogPrint.log("accept client interrupted message");
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
        Channel channel = ctx.channel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        InetAddress address = socketAddress.getAddress();
        LogPrint.log("client connection closed：" + address);
        ctx.close();
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        ndcServerConfigCenter.unRegisterMessageChannel(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogPrint.err("unCatchable server error：" + cause.getMessage());



        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();

        //for the client local is remote
        NDCMessageProtocol of = NDCMessageProtocol.of(localAddress.getAddress(), remoteAddress.getAddress(), 0, localAddress.getPort(), remoteAddress.getPort(), NDCMessageProtocol.UN_CATCHABLE_ERROR);
        of.setData(cause.toString().getBytes());
        ctx.writeAndFlush(of).addListeners(ChannelFutureListener.CLOSE);

    }


}
