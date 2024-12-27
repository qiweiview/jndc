package com.view.core.server.tcp;

import com.view.core.model.TCPDataTransport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Data
@Slf4j
public class ByteServerHandler extends SimpleChannelInboundHandler<byte[]> {
    private TCPServerConfiguration tcpServerConfiguration;

    private String appServerSessionId;

    private TCPServer tcpServer;

    private ChannelHandlerContext channelHandlerContext;

    private LinkedBlockingQueue<byte[]> bufferQueue = new LinkedBlockingQueue();

    private long connectedTime;

    private long recentActiveTime;

    private volatile boolean activeCompleted = false;

    private volatile boolean directWrite = false;


    public ByteServerHandler(TCPServer tcpServer, TCPServerConfiguration tcpServerConfiguration) {
        this.tcpServerConfiguration = tcpServerConfiguration;
        this.tcpServer = tcpServer;
    }

    /**
     * 客户端连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        String longText = channel.id().asLongText();
        TCPDataTransport tcpDataTransport = new TCPDataTransport();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        tcpDataTransport.setRemote(socketAddress);
        tcpDataTransport.setTcpChannelId(longText);

        tcpServerConfiguration.getActiveCallBack().accept(tcpDataTransport, ctx);
    }


    /**
     * 客户端断开连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String longText = channel.id().asLongText();
        TCPDataTransport tcpDataTransport = new TCPDataTransport();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        tcpDataTransport.setRemote(socketAddress);
        tcpDataTransport.setTcpChannelId(longText);
        tcpServerConfiguration.getInactiveCallBack().accept(tcpDataTransport, tcpServer);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        String hostString = socketAddress.getHostString();
        int port = socketAddress.getPort();


        log.error("TCP服务端{}:{}异常，准备通知客户端断开连接", hostString, port, cause);
        //当作连接断开处理
        //channelInactive(ctx);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {

        Consumer<TCPDataTransport> readCallBack = tcpServerConfiguration.getReadCallBack();
        Channel channel = ctx.channel();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        TCPDataTransport dataTransport = new TCPDataTransport();
        dataTransport.setData(msg);
        dataTransport.setRemote(inetSocketAddress);
        String longText = channel.id().asLongText();
        dataTransport.setTcpChannelId(longText);


        readCallBack.accept(dataTransport);
    }
}
