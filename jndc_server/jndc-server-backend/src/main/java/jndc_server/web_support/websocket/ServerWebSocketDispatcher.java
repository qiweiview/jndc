package jndc_server.web_support.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jndc.core.UniqueBeanManage;
import jndc.utils.JSONUtils;
import jndc.web_support.core.WebSocketChannelAttrs;
import jndc.web_support.core.WebSocketEventDispatcher;
import jndc_server.core.ServerTerminalSessionManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerWebSocketDispatcher implements WebSocketEventDispatcher {

    @Override
    public void afterHandshake(Channel channel) {
        UniqueBeanManage.getBean(ServerTerminalSessionManager.class).afterHandshake(channel);
    }

    @Override
    public void handleTextFrame(ChannelHandlerContext channelHandlerContext, String text) {
        String mode = channelHandlerContext.channel().attr(WebSocketChannelAttrs.MODE).get();
        if (!ServerTerminalSessionManager.MODE_TERMINAL.equals(mode)) {
            log.debug("ignore websocket message in mode {}", mode);
            return;
        }

        TerminalWebSocketRequest request = JSONUtils.str2Object(text, TerminalWebSocketRequest.class);
        UniqueBeanManage.getBean(ServerTerminalSessionManager.class).handleBrowserMessage(channelHandlerContext, request);
    }

    @Override
    public void handleChannelInactive(ChannelHandlerContext channelHandlerContext) {
        String mode = channelHandlerContext.channel().attr(WebSocketChannelAttrs.MODE).get();
        if (ServerTerminalSessionManager.MODE_TERMINAL.equals(mode)) {
            UniqueBeanManage.getBean(ServerTerminalSessionManager.class).handleBrowserDisconnect(channelHandlerContext.channel());
        }
    }
}
