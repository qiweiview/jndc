package com.view.core.client.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerWebsocketClientHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //发送心跳
//        ctx.channel().writeAndFlush(new PingWebSocketFrame());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;
            log.info("收到消息：" + textWebSocketFrame.text());
        } else if (msg instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) msg;
            ByteBuf content = binaryWebSocketFrame.content();
            log.info("收到消息：" + content.toString(CharsetUtil.UTF_8));
        } else if (msg instanceof PongWebSocketFrame) {
            log.info("收到pong消息");
        } else if (msg instanceof CloseWebSocketFrame) {
            log.info("收到关闭消息");
            ctx.close();
        } else if (msg instanceof PingWebSocketFrame) {
            log.info("收到ping消息");
        } else {
            log.error("不支持的消息类型");
        }
    }
}