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
        // 判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            log.info("关闭链路");
            ctx.close();
            return;
        }
        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            log.info("获取到心跳消息");
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 只处理文本消息，不处理二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            log.error("只支持文本消息");
            throw new UnsupportedOperationException("只支持文本消息");
        }

        // 处理文本消息
        String text = ((TextWebSocketFrame) frame).text();
        log.info("收到消息：" + text);

        // 响应消息
        ctx.channel().writeAndFlush(new TextWebSocketFrame("收到消息：" + text));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("捕获异常：", cause);
        ctx.close();
    }
}
