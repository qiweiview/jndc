package jndc.port_redirect;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;

public class ProxyClient extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext innerChannelHandlerContext;//本地连接

    private ChannelHandlerContext outerChannelHandlerContext;//要转发的远程连接

    public ProxyClient(ChannelHandlerContext outerChannelHandlerContext) {
        this.outerChannelHandlerContext = outerChannelHandlerContext;//赋值要转发连接的outerChannelHandlerContext
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.innerChannelHandlerContext=ctx;//赋值本地连接
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.outerChannelHandlerContext.write(msg);//把本地读取到的消息写入到远程连接
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.outerChannelHandlerContext.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.outerChannelHandlerContext.fireChannelInactive();
    }

    public void write(Object o){
        this.innerChannelHandlerContext.write(o);
    }

    public void writeAndFlush(Object o){
        this.innerChannelHandlerContext.writeAndFlush(o);
    }


}
