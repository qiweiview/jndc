package jndc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;


public class ClientTCPDataHandle extends ChannelInboundHandlerAdapter {
    public static final String NAME = "NDC_CLIENT_TCP_DATA_HANDLE";

    private ChannelHandlerContext channelHandlerContext;

    private NDCMessageProtocol messageModel;

    public ClientTCPDataHandle(NDCMessageProtocol messageModel) {
        this.messageModel = messageModel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channelHandlerContext=ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = ByteBufUtil.getBytes(byteBuf);


        //发送消息
        NDCMessageProtocol copy = messageModel.copy();
        copy.inetSwap();
        copy.setType(NDCMessageProtocol.TCP_DATA);
        copy.setData(bytes);
        UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, remoteAddress.getPort(), localAddress.getPort(), innerHandlerCallBack.getLocalPort(), NDCMessageProtocol.SYSTEM_ERROR);
//        ndcMessageProtocol.setData("connection lose".getBytes());
//        UniqueBeanManage.getBean(NDCServerConfigCenter.class).addMessageToSendQueue(ndcMessageProtocol);

        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public void close(){
        if (channelHandlerContext!=null){
            channelHandlerContext.close();
        }
    }


    public void writeMessage(ByteBuf byteBuf){
        channelHandlerContext.writeAndFlush(byteBuf);
    }

}
