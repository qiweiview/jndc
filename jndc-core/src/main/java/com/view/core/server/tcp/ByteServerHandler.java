package com.view.core.server.tcp;

import com.view.core.component.GlobalBeanContext;
import com.view.core.model.TCPDataTransport;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@Slf4j
public class ByteServerHandler extends SimpleChannelInboundHandler<byte[]> {
    private String appServerSessionId;

    private TCPServer tcpServer;

    private ChannelHandlerContext channelHandlerContext;

    private LinkedBlockingQueue<byte[]> bufferQueue = new LinkedBlockingQueue();

    private long connectedTime;

    private long recentActiveTime;

    //10分钟超时
    private long timeoutLimit = 10 * 60 * 1000;

    private volatile boolean activeCompleted = false;

    private volatile boolean directWrite = false;


    public ByteServerHandler(TCPServer tcpServer) {
        this.tcpServer = tcpServer;
        this.connectedTime = System.currentTimeMillis();
    }

    /**
     * 客户端连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = socketAddress.getHostString();
        boolean block = GlobalBeanContext.IP_BLOCKER.checkBlock(ip);
        if (block) {
            log.info("IP:{}被阻断", ip);
            ctx.close();
            return;
        }

        boolean b = GlobalBeanContext.TIME_BLOCKER.checkBlock(LocalTime.now());
        if (b) {
            log.info("当前时间被阻断");
            ctx.close();
            return;
        }


        appServerSessionId = ctx.channel().id().asLongText();

        channelHandlerContext = ctx;
        //注册会话
        tcpServer.registerSession(appServerSessionId, this);


        TCPDataTransport tcpDataTransport = createTCPDataTransport();

        //客户端信息
        String ndcClientId = tcpServer.getNdcClientId();

        //构建报文
        NDCPacket ndcPacket = NDCPacketBuilder.tcpActivePacket(tcpDataTransport);
        GlobalBeanContext.NDC_SERVER.write(ndcClientId, ndcPacket);
    }

    /**
     * 读、写都算活跃
     */
    private void active() {
        this.recentActiveTime = System.currentTimeMillis();
    }


    /**
     * 客户端断开连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //取消当前会话注册
        tcpServer.unRegisterSession(appServerSessionId);

        //向通道发送关闭消息
        TCPDataTransport tcpDataTransport = createTCPDataTransport();
        NDCPacket ndcPacket = NDCPacketBuilder.tcpInactivePacket(tcpDataTransport);
        GlobalBeanContext.NDC_SERVER.write(tcpServer.getNdcClientId(), ndcPacket);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("TCP服务端异常，准备通知客户端断开连接", cause);
        //当作连接断开处理
        channelInactive(ctx);
    }

    /**
     * 创建TCP数据传输对象
     *
     * @return
     */
    private TCPDataTransport createTCPDataTransport() {
        TCPDataTransport tcpDataTransport = new TCPDataTransport();

        //服务端信息
        tcpDataTransport.setNdcServerId(tcpServer.getNdcServerId());
        tcpDataTransport.setAppServerId(tcpServer.getAppServerId());
        tcpDataTransport.setAppServerSessionId(appServerSessionId);

        //客户端信息
        String ndcClientId = tcpServer.getNdcClientId();
        tcpDataTransport.setNdcClientId(ndcClientId);
        tcpDataTransport.setClientServiceId(tcpServer.getClientServiceId());

        return tcpDataTransport;
    }


    /**
     * 缓冲区刷新
     */
    private void bufferFlush() {
        if (!bufferQueue.isEmpty()) {
            synchronized (this) {
                if (!bufferQueue.isEmpty()) {
                    bufferQueue.forEach(this::writeDataIntoChannel);
                    bufferQueue.clear();
                    directWrite = true;
                    log.info("缓冲区刷新完成");
                }
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        active();

        if (activeCompleted) {
            //todo 准备好了直接写入

            if (directWrite) {
                writeDataIntoChannel(msg);
            } else {
                bufferFlush();
                writeDataIntoChannel(msg);
            }

        } else {
            //todo 没有准备好则

            bufferQueue.add(msg);
        }

    }

    private void writeDataIntoChannel(byte[] bytes) {
        active();

        TCPDataTransport tcpDataTransport = createTCPDataTransport();
        tcpDataTransport.setData(bytes);

        //客户端信息
        String ndcClientId = tcpServer.getNdcClientId();


        //构建报文
        NDCPacket ndcPacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
        GlobalBeanContext.NDC_SERVER.write(ndcClientId, ndcPacket);
    }

    /**
     * 通知客户端已经就绪
     */
    public void noticeActiveCompleted() {
        active();
        bufferFlush();
        activeCompleted = true;
    }

    public boolean idleOverLimit() {
        return System.currentTimeMillis() - recentActiveTime > timeoutLimit;
    }

    public void close() {
        channelHandlerContext.close();
        bufferQueue.clear();
        log.info("会话{}因超时关闭", appServerSessionId);
    }
}
