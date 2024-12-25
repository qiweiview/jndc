package com.view.core.client.tcp;

import com.view.core.component.SupportEnvironment;
import com.view.core.model.TCPDataTransport;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Data
@Slf4j
public class ByteClientHandler extends SimpleChannelInboundHandler<byte[]> {
    private TCPClientConfiguration tcpClientConfiguration;

    private  SupportEnvironment supportEnvironment;

    private TCPClient tcpClient;

    private ChannelHandlerContext ctx;

    public ByteClientHandler(TCPClient tcpClient, TCPClientConfiguration tcpClientConfiguration) {
        this.tcpClient = tcpClient;
        this.tcpClientConfiguration = tcpClientConfiguration;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;

        TCPDataTransport tcpDataTransport = createTCPDataTransport();
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        tcpDataTransport.setRemote(socketAddress);
        tcpClientConfiguration.getActiveCallBack().accept(tcpDataTransport, tcpClient);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        TCPDataTransport tcpDataTransport = createTCPDataTransport();
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        tcpDataTransport.setRemote(socketAddress);
        tcpClientConfiguration.getReadCompleteCallBack().accept(tcpDataTransport, tcpClient);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        TCPDataTransport dataTransport = new TCPDataTransport();
        dataTransport.setData(msg);
        dataTransport.setRemote(inetSocketAddress);
        //tcp
        tcpClientConfiguration.getReadCallBack().accept(dataTransport);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.error("{}:{}  {}:{}TCP客户端异常，准备通知远程断开连接:",
                localAddress.getHostName(),
                localAddress.getPort(),
                remoteAddress.getHostName(),
                remoteAddress.getPort(),
                cause);
        //当作断开连接处理
        //channelInactive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //向通道发送关闭消息
        TCPDataTransport tcpDataTransport = createTCPDataTransport();
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        tcpDataTransport.setRemote(socketAddress);
        tcpClientConfiguration.getInactiveCallBack().accept(tcpDataTransport, tcpClient);

        //组包，发送
        //writePackageIntoChannel(tcpDataTransport,socketAddress);
    }


    /**
     * 创建TCP数据传输对象
     *
     * @return
     */
    private TCPDataTransport createTCPDataTransport() {
        TCPDataTransport tcpDataTransport = new TCPDataTransport();

        //服务端信息

        tcpDataTransport.setAppServerId(tcpClient.getAppServerId());
        tcpDataTransport.setAppServerSessionId(tcpClient.getAppServerSessionId());

        //客户端信息

        tcpDataTransport.setClientServiceId(tcpClient.getClientServiceId());
        tcpDataTransport.setClientServiceSessionId(tcpClient.getClientServiceSessionId());

        return tcpDataTransport;
    }


    public void write(byte[] bytes) {
        if (ctx != null) {
            ctx.writeAndFlush(bytes);

        } else {
            log.warn("ChannelHandlerContext is null");
        }
    }
}
