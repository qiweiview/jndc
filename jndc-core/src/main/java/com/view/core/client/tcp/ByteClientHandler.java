package com.view.core.client.tcp;

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


    private TCPClient tcpClient;

    private ChannelHandlerContext ctx;

    public ByteClientHandler(TCPClient tcpClient, TCPClientConfiguration tcpClientConfiguration) {
        this.tcpClient = tcpClient;
        this.tcpClientConfiguration = tcpClientConfiguration;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        tcpClientConfiguration.getActiveCallBack().accept(ctx);
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
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        TCPDataTransport dataTransport = new TCPDataTransport();
        dataTransport.setRemote(inetSocketAddress);
        tcpClientConfiguration.getReadCompleteCallBack().accept(dataTransport, tcpClient);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        TCPDataTransport dataTransport = new TCPDataTransport();
        dataTransport.setRemote(inetSocketAddress);
        tcpClientConfiguration.getInactiveCallBack().accept(dataTransport);
    }





    public void write(byte[] bytes) {
        if (ctx != null) {
            ctx.writeAndFlush(bytes);

        } else {
            log.warn("ChannelHandlerContext is null");
        }
    }
}
