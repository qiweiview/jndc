package jndc.port_redirect;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.utils.LogPrint;

import java.net.InetSocketAddress;

public class LocalInHandle extends ChannelInboundHandlerAdapter {
    private ProxyClient proxyClient;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        this.proxyClient = new ProxyClient(ctx);
        bootstrap.channel(NioSocketChannel.class).handler(proxyClient);
        bootstrap.group(ctx.channel().eventLoop());
        ChannelFuture connect = bootstrap.connect(new InetSocketAddress("127.0.0.1", 3306));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        EventLoop eventExecutors = ctx.channel().eventLoop();
        eventExecutors.shutdownGracefully().addListener(x -> {
            LogPrint.log("stop");
        });

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.proxyClient.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.proxyClient.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

}
