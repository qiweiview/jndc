package com.view.core.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 获取请求的URI
        String uri = request.uri();

        // 判断请求是否为favicon.ico，如果是则忽略
        if ("/favicon.ico".equals(uri)) {
            ctx.writeAndFlush(DefaultFullHttpResponse.EMPTY_LAST_CONTENT)
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        }

        // 构造响应内容
        ByteBuf content = Unpooled.copiedBuffer("Hello, World!", CharsetUtil.UTF_8);

        // 构造响应对象
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

        // 设置响应头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

        // 发送响应
        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常：", cause);
    }
}