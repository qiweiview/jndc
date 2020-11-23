package web.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import jndc.core.UniqueBeanManage;
import jndc.utils.LogPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketHandle extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String NAME = "WEB_SOCKET_HANDLE";

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        LogPrint.info("channelRead0" + textWebSocketFrame);
        channelHandlerContext.writeAndFlush(textWebSocketFrame.retain());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            Channel channel = ctx.channel();
            MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
            messageNotificationCenter.websocketRegister(channel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
