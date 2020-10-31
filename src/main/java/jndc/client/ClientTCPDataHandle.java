package jndc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.server.NDCServerConfigCenter;
import jndc.utils.ByteBufUtil4V;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;

import java.net.InetSocketAddress;


public class ClientTCPDataHandle extends ChannelInboundHandlerAdapter {
    public static final String NAME = "NDC_CLIENT_TCP_DATA_HANDLE";

    private ChannelHandlerContext channelHandlerContext;

    private NDCMessageProtocol messageModel;

    public ClientTCPDataHandle(NDCMessageProtocol messageModel) {
        this.messageModel = messageModel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channelHandlerContext = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = ByteBufUtil4V.readWithRelease(byteBuf);
        byteBuf.discardReadBytes();
        byteBuf.release();

        //发送消息
        NDCMessageProtocol copy = messageModel.copy();
        copy.setType(NDCMessageProtocol.TCP_DATA);
        copy.setData(bytes);
        UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        //发送消息
        NDCMessageProtocol copy = messageModel.copy();
        copy.setType(NDCMessageProtocol.CONNECTION_INTERRUPTED);
        copy.setData("connection lose".getBytes());

        UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogPrint.err("client tcp get a unCatchable error, cause:" + cause);
    }

    public void close() {
        if (channelHandlerContext != null) {
            channelHandlerContext.close();
        }
    }


    public void writeMessage(ByteBuf byteBuf) {
        channelHandlerContext.writeAndFlush(byteBuf);
    }

}
