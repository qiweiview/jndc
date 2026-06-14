package jndc_client.core.port_app;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.utils.ByteBufUtil4V;
import jndc_client.core.JNDCClientConfig;
import jndc_client.core.JNDCClientConfigCenter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@Slf4j
public class ClientTCPDataHandle extends ChannelInboundHandlerAdapter {

    private static final int MAX_PENDING_BUFFER_COUNT = 128;

    private static final int MAX_PENDING_BUFFER_BYTES = 1024 * 1024;

    private final String tId = UUID.randomUUID().toString();

    private long createTime;

    private volatile long lastActiveTime;

    //是否废弃
    private volatile boolean released = false;

    public static final String NAME = "NDC_CLIENT_TCP_DATA_HANDLE";

    private ChannelHandlerContext channelHandlerContext;

    private NDCMessageProtocol messageModel;

    // 连接建立前缓存的待发送数据
    private final List<ByteBuf> pendingBuffers = new ArrayList<>();

    private volatile int pendingBufferBytes;

    private volatile ChannelFuture connectFuture;

    private final Runnable releaseCallback;

    private volatile boolean interruptSent = false;


    public ClientTCPDataHandle(NDCMessageProtocol messageModel, Runnable releaseCallback) {
        this.messageModel = messageModel;
        this.releaseCallback = releaseCallback;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("local app has been active ");
        this.channelHandlerContext = ctx;
        // 连接建立后，发送所有缓存的数据
        flushPendingBuffers();
    }

    /**
     * 发送所有缓存的待发送数据
     */
    private void flushPendingBuffers() {
        synchronized (pendingBuffers) {
            for (ByteBuf buf : pendingBuffers) {
                channelHandlerContext.writeAndFlush(buf);
            }
            pendingBuffers.clear();
            pendingBufferBytes = 0;
        }
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

        if (channelHandlerContext != null) {
            channelHandlerContext.writeAndFlush(byteBuf);
        } else {
            // 连接尚未建立，缓存数据
            synchronized (pendingBuffers) {
                if (channelHandlerContext != null) {
                    // 双重检查：可能在等待锁期间连接已建立
                    channelHandlerContext.writeAndFlush(byteBuf);
                } else {
                    int nextBytes = pendingBufferBytes + byteBuf.readableBytes();
                    if (pendingBuffers.size() >= MAX_PENDING_BUFFER_COUNT || nextBytes > MAX_PENDING_BUFFER_BYTES) {
                        log.error("pending buffers overflow, drop local forward connection " + tId);
                        ReferenceCountUtil.release(byteBuf);
                        notifyRemoteConnectionInterrupted();
                        releaseRelatedResources();
                        return;
                    }
                    pendingBuffers.add(byteBuf);
                    pendingBufferBytes = nextBytes;
                }
            }
        }
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
        notifyRemoteConnectionInterrupted();

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
        if (released) {
            return;
        }
        released = true;
        if (connectFuture != null && !connectFuture.isDone()) {
            connectFuture.cancel(true);
        }
        if (channelHandlerContext != null) {
            //关闭到本地服务的连接
            channelHandlerContext.close();
            channelHandlerContext = null;
        }
        synchronized (pendingBuffers) {
            pendingBuffers.forEach(ReferenceCountUtil::release);
            pendingBuffers.clear();
            pendingBufferBytes = 0;
        }
        if (releaseCallback != null) {
            releaseCallback.run();
        }
    }

    public void notifyRemoteConnectionInterrupted() {
        if (interruptSent) {
            return;
        }
        interruptSent = true;
        NDCMessageProtocol copy = messageModel.copy();
        copy.setType(NDCMessageProtocol.CONNECTION_INTERRUPTED);
        copy.setData(NDCMessageProtocol.BLANK);
        UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(copy);
    }
}
