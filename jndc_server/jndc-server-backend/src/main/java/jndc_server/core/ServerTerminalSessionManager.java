package jndc_server.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.message.OpenChannelMessage;
import jndc.core.message.TerminalControlMessage;
import jndc.utils.InetUtils;
import jndc.utils.JSONUtils;
import jndc.utils.ObjectSerializableUtils;
import jndc.web_support.core.MessageNotificationCenter;
import jndc.web_support.core.WebSocketChannelAttrs;
import jndc_server.web_support.websocket.TerminalWebSocketRequest;
import jndc_server.web_support.websocket.TerminalWebSocketResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServerTerminalSessionManager {

    public static final String MODE_NOTIFY = "notify";
    public static final String MODE_TERMINAL = "terminal";

    private final Map<String, Channel> sessionChannelMap = new ConcurrentHashMap<>();
    private final Map<String, String> sessionClientMap = new ConcurrentHashMap<>();
    private final Map<String, String> clientSessionMap = new ConcurrentHashMap<>();
    private final Map<String, String> channelSessionMap = new ConcurrentHashMap<>();

    public void afterHandshake(Channel channel) {
        if (MODE_NOTIFY.equals(resolveMode(channel))) {
            UniqueBeanManage.getBean(MessageNotificationCenter.class).websocketRegister(channel);
        }
    }

    public void handleBrowserMessage(ChannelHandlerContext channelHandlerContext, TerminalWebSocketRequest request) {
        if (request == null || request.getEvent() == null) {
            sendBrowserError(channelHandlerContext.channel(), null, "terminal event 缺失");
            return;
        }

        switch (request.getEvent()) {
            case TerminalWebSocketRequest.EVENT_OPEN:
                openSession(channelHandlerContext.channel(), request);
                break;
            case TerminalWebSocketRequest.EVENT_INPUT:
                forwardControl(channelHandlerContext.channel(), request, TerminalControlMessage.ACTION_INPUT);
                break;
            case TerminalWebSocketRequest.EVENT_RESIZE:
                forwardControl(channelHandlerContext.channel(), request, TerminalControlMessage.ACTION_RESIZE);
                break;
            case TerminalWebSocketRequest.EVENT_CLOSE:
                closeSessionFromBrowser(channelHandlerContext.channel(), request.getSessionId());
                break;
            default:
                sendBrowserError(channelHandlerContext.channel(), request.getSessionId(), "unsupported terminal event: " + request.getEvent());
                break;
        }
    }

    public void handleBrowserDisconnect(Channel channel) {
        String sessionId = channelSessionMap.get(channel.id().asLongText());
        if (sessionId == null) {
            return;
        }

        String clientId = sessionClientMap.get(sessionId);
        if (clientId != null) {
            sendTerminalControlMessage(clientId, terminalMessage(TerminalControlMessage.ACTION_CLOSE, sessionId, clientId));
        }
        cleanupSession(sessionId);
    }

    public void handleClientMessage(TerminalControlMessage message) {
        if (message == null || message.getSessionId() == null) {
            return;
        }

        switch (message.getAction()) {
            case TerminalControlMessage.ACTION_OPEN:
                sendBrowserFrame(message.getSessionId(), TerminalWebSocketResponse.ready(message.getSessionId(), message.getShellType()));
                break;
            case TerminalControlMessage.ACTION_OUTPUT:
                sendBrowserFrame(message.getSessionId(), TerminalWebSocketResponse.output(message.getSessionId(), message.getData()));
                break;
            case TerminalControlMessage.ACTION_EXIT:
                sendBrowserFrame(message.getSessionId(), TerminalWebSocketResponse.exit(message.getSessionId(), message.getExitCode()));
                cleanupSession(message.getSessionId());
                break;
            case TerminalControlMessage.ACTION_ERROR:
                sendBrowserFrame(message.getSessionId(), TerminalWebSocketResponse.error(message.getSessionId(), message.getMessage()));
                cleanupSession(message.getSessionId());
                break;
            case TerminalControlMessage.ACTION_CLOSE:
                cleanupSession(message.getSessionId());
                break;
            default:
                log.warn("ignore unexpected terminal message action from client: {}", message.getAction());
                break;
        }
    }

    public void handleClientOffline(String clientId) {
        if (clientId == null) {
            return;
        }
        String sessionId = clientSessionMap.get(clientId);
        if (sessionId == null) {
            return;
        }
        sendBrowserFrame(sessionId, TerminalWebSocketResponse.error(sessionId, "设备离线，终端会话已关闭"));
        cleanupSession(sessionId);
    }

    private synchronized void openSession(Channel channel, TerminalWebSocketRequest request) {
        String sessionId = request.getSessionId();
        String clientId = request.getClientId();
        if (isBlank(sessionId)) {
            sendBrowserError(channel, null, "sessionId 不能为空");
            return;
        }
        if (isBlank(clientId)) {
            sendBrowserError(channel, sessionId, "clientId 不能为空");
            return;
        }
        if (channelSessionMap.containsKey(channel.id().asLongText())) {
            sendBrowserError(channel, sessionId, "当前连接已有活动终端会话");
            return;
        }
        if (sessionChannelMap.containsKey(sessionId)) {
            sendBrowserError(channel, sessionId, "sessionId 已存在");
            return;
        }
        if (clientSessionMap.containsKey(clientId)) {
            sendBrowserError(channel, sessionId, "当前设备已有活动终端会话");
            return;
        }

        NDCServerConfigCenter configCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        ChannelHandlerContextHolder holder = configCenter.getContextHolder(clientId);
        if (holder == null) {
            sendBrowserError(channel, sessionId, "目标设备不在线");
            return;
        }
        if (holder.getAuthMode() != OpenChannelMessage.FULL_AUTHORIZED) {
            sendBrowserError(channel, sessionId, "目标设备未开启 FULL_AUTHORIZED");
            return;
        }

        sessionChannelMap.put(sessionId, channel);
        sessionClientMap.put(sessionId, clientId);
        clientSessionMap.put(clientId, sessionId);
        channelSessionMap.put(channel.id().asLongText(), sessionId);

        TerminalControlMessage message = terminalMessage(TerminalControlMessage.ACTION_OPEN, sessionId, clientId);
        message.setCols(request.getCols());
        message.setRows(request.getRows());
        if (!sendTerminalControlMessage(clientId, message)) {
            cleanupSession(sessionId);
            sendBrowserError(channel, sessionId, "终端打开失败，设备连接不可用");
        }
    }

    private synchronized void forwardControl(Channel channel, TerminalWebSocketRequest request, String action) {
        String sessionId = request.getSessionId();
        if (!ownsSession(channel, sessionId)) {
            sendBrowserError(channel, sessionId, "terminal session 不存在");
            return;
        }
        String clientId = sessionClientMap.get(sessionId);
        if (clientId == null) {
            sendBrowserError(channel, sessionId, "terminal session 已失效");
            cleanupSession(sessionId);
            return;
        }

        TerminalControlMessage message = terminalMessage(action, sessionId, clientId);
        message.setData(request.getData());
        message.setCols(request.getCols());
        message.setRows(request.getRows());
        if (!sendTerminalControlMessage(clientId, message)) {
            sendBrowserError(channel, sessionId, "终端控制消息发送失败");
            cleanupSession(sessionId);
        }
    }

    private synchronized void closeSessionFromBrowser(Channel channel, String sessionId) {
        if (!ownsSession(channel, sessionId)) {
            sendBrowserError(channel, sessionId, "terminal session 不存在");
            return;
        }
        String clientId = sessionClientMap.get(sessionId);
        if (clientId != null) {
            sendTerminalControlMessage(clientId, terminalMessage(TerminalControlMessage.ACTION_CLOSE, sessionId, clientId));
        }
    }

    private synchronized void cleanupSession(String sessionId) {
        if (sessionId == null) {
            return;
        }
        Channel channel = sessionChannelMap.remove(sessionId);
        String clientId = sessionClientMap.remove(sessionId);
        if (clientId != null) {
            clientSessionMap.remove(clientId);
        }
        if (channel != null) {
            channelSessionMap.remove(channel.id().asLongText());
        }
    }

    private boolean sendTerminalControlMessage(String clientId, TerminalControlMessage terminalControlMessage) {
        NDCServerConfigCenter configCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        ChannelHandlerContextHolder holder = configCenter.getContextHolder(clientId);
        if (holder == null || holder.getChannelHandlerContext() == null) {
            return false;
        }

        NDCMessageProtocol protocol = NDCMessageProtocol.of(
                InetUtils.localInetAddress,
                InetUtils.localInetAddress,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.TERMINAL_CONTROL
        );
        protocol.setData(ObjectSerializableUtils.object2bytes(terminalControlMessage));
        holder.getChannelHandlerContext().writeAndFlush(protocol);
        return true;
    }

    private void sendBrowserFrame(String sessionId, TerminalWebSocketResponse response) {
        Channel channel = sessionChannelMap.get(sessionId);
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtils.object2JSONString(response)));
    }

    private void sendBrowserError(Channel channel, String sessionId, String message) {
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(new TextWebSocketFrame(
                JSONUtils.object2JSONString(TerminalWebSocketResponse.error(sessionId, message))
        ));
    }

    private boolean ownsSession(Channel channel, String sessionId) {
        if (channel == null || sessionId == null) {
            return false;
        }
        String expectedSessionId = channelSessionMap.get(channel.id().asLongText());
        return sessionId.equals(expectedSessionId);
    }

    private String resolveMode(Channel channel) {
        String mode = channel.attr(WebSocketChannelAttrs.MODE).get();
        if (mode == null || "".equals(mode.trim())) {
            return MODE_NOTIFY;
        }
        return mode;
    }

    private boolean isBlank(String value) {
        return value == null || "".equals(value.trim());
    }

    private TerminalControlMessage terminalMessage(String action, String sessionId, String clientId) {
        TerminalControlMessage message = new TerminalControlMessage();
        message.setAction(action);
        message.setSessionId(sessionId);
        message.setClientId(clientId);
        return message;
    }
}
