package jndc_client.core.port_app;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.utils.ByteBufUtil4V;
import jndc_client.core.JNDCClientConfig;
import jndc_client.core.JNDCClientConfigCenter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;


@Data
@Slf4j
public class ClientTCPDataHandle extends ChannelInboundHandlerAdapter {

    private final String tId = UUID.randomUUID().toString();

    private long createTime;

    private volatile long lastActiveTime;

    //是否废弃
    private volatile boolean released = false;

    public static final String NAME = "NDC_CLIENT_TCP_DATA_HANDLE";

    private ChannelHandlerContext channelHandlerContext;

    private NDCMessageProtocol messageModel;


    public ClientTCPDataHandle(NDCMessageProtocol messageModel) {
        this.messageModel = messageModel;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("local app has been active ");
        this.channelHandlerContext = ctx;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        updateTime();

        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = ByteBufUtil4V.readWithRelease(byteBuf);


        //发送消息
        NDCMessageProtocol copy = messageModel.copy();
        copy.setType(NDCMessageProtocol.TCP_DATA);
        copy.setData(bytes);
        UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);
    }

    public void receiveMessage(ByteBuf byteBuf) {
        updateTime();

        channelHandlerContext.writeAndFlush(byteBuf);
    }

    /**
     * 变更活跃时间
     */
    private void updateTime() {
        this.lastActiveTime = System.currentTimeMillis();
    }

    /**
     * 是否过期
     *
     * @return
     */
    public boolean isTimeOut() {
        long l = System.currentTimeMillis();
        long times = l - lastActiveTime;
        JNDCClientConfig jndcClientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);
        boolean b = times > jndcClientConfig.getAutoReleaseTimeOut();
        if (b) {
            log.debug("now:" + l + " last active time:" + lastActiveTime + " difference:" + times + " time out limit:" + jndcClientConfig.getAutoReleaseTimeOut());
        }
        return b;
    }

    /**
     * 服务由本地中断
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        log.debug("local app inactive ");


        //发送中断消息给服务端
        NDCMessageProtocol copy = messageModel.copy();
        copy.setType(NDCMessageProtocol.CONNECTION_INTERRUPTED);
        copy.setData(NDCMessageProtocol.BLANK);
        UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);

        //释放本地连接
        releaseRelatedResources();


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.error("client get a exception: " + cause);
    }


    /**
     * 释放本地连接
     */
    public void releaseRelatedResources() {
        released = true;
        if (channelHandlerContext != null) {
            //关闭到本地服务的连接
            channelHandlerContext.close();
            channelHandlerContext = null;
        }
    }
}
