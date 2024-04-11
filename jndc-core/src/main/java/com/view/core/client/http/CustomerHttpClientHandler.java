package com.view.core.client.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerHttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private String activePath;

    public CustomerHttpClientHandler(String activePath) {
        this.activePath = activePath;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/test");
        //发送请求
        ctx.writeAndFlush(request);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpResponse fullHttpResponse) throws Exception {
        //获取响应状态
        HttpResponseStatus status = fullHttpResponse.status();
        //获取响应头
        HttpHeaders headers = fullHttpResponse.headers();
        //获取响应体
        ByteBuf content = fullHttpResponse.content();
        String response = content.toString(CharsetUtil.UTF_8);
        log.info("响应状态：" + status);
        log.info("响应头：" + headers);
        log.info("响应体：" + response);
    }
}