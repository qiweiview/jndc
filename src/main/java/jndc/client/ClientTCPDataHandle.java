package jndc.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;

import jndc.utils.ByteBufUtil4V;

import jndc.utils.LogPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientTCPDataHandle extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

        //发送消息
        NDCMessageProtocol copy = messageModel.copy();
        copy.setType(NDCMessageProtocol.TCP_DATA);
        copy.setData(bytes);
        UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        releaseRelatedResources();


        //发送消息
        NDCMessageProtocol copy = messageModel.copy();
        copy.setType(NDCMessageProtocol.CONNECTION_INTERRUPTED);
        copy.setData(NDCMessageProtocol.BLANK);
        UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);
        //logger.info("client send interrupt signal ");


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client get a exception: "+cause);
    }

    public void receiveMessage(ByteBuf byteBuf) {
        channelHandlerContext.writeAndFlush(byteBuf);
    }

    public void releaseRelatedResources() {
        if (channelHandlerContext!=null){
            channelHandlerContext.close();
            channelHandlerContext=null;
        }
        //logger.info("release local connection");
    }
}
