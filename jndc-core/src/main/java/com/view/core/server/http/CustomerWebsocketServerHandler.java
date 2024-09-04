package com.view.core.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerWebsocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            ctx.writeAndFlush(new TextWebSocketFrame("欢迎！连接已建立"));
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof CloseWebSocketFrame) {
            log.info("ws server关闭链路");
            ctx.close();
        } else if (frame instanceof PingWebSocketFrame) {
            log.info("ws server获取到心跳消息");
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        } else if (frame instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            log.info("ws server收到TextWebSocketFrame消息：{}", text);

            // 响应消息
            ctx.channel().writeAndFlush(new TextWebSocketFrame("收到消息：" + text));
        } else {
            log.error("ws server只支持文本消息");
            throw new UnsupportedOperationException("只支持文本消息");
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("捕获异常：", cause);
        ctx.close();
    }
}
