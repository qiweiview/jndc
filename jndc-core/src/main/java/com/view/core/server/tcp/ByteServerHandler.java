package com.view.core.server.tcp;

import com.view.core.component.GlobalBeanContext;
import com.view.core.model.TCPDataTransport;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

@Data
@Slf4j
public class ByteServerHandler extends SimpleChannelInboundHandler<byte[]> {
    private String appServerSessionId;

    private TCPServer tcpServer;

    private ChannelHandlerContext channelHandlerContext;

    private LinkedBlockingQueue<byte[]> bufferQueue = new LinkedBlockingQueue();

    private long connectedTime;

    private long firstPackageTime;

    //15秒启动超时
    private long timeoutLimit = 15 * 1000;

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
     * 客户端断开连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

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

            long now = System.currentTimeMillis();
            if (firstPackageTime == 0) {
                firstPackageTime = System.currentTimeMillis();
                bufferQueue.add(msg);
            } else if (now - firstPackageTime > timeoutLimit) {
                //直接关闭，清理bufferQueue
                bufferQueue.clear();
                ctx.close();
                log.warn("连接超时，关闭连接");
            } else {
                bufferQueue.add(msg);
            }

        }

    }

    private void writeDataIntoChannel(byte[] bytes) {

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
        bufferFlush();
        activeCompleted = true;
    }

}
