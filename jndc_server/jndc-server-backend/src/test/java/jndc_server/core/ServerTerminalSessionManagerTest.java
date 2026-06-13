package jndc_server.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.message.TerminalControlMessage;
import jndc.utils.JSONUtils;
import jndc_server.web_support.websocket.TerminalWebSocketRequest;
import jndc_server.web_support.websocket.TerminalWebSocketResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServerTerminalSessionManagerTest {

    @Before
    public void setUp() throws Exception {
        clearBeanMap();
    }

    @After
    public void tearDown() throws Exception {
        clearBeanMap();
    }

    @Test
    public void shouldRejectOfflineClient() throws Exception {
        NDCServerConfigCenter configCenter = new NDCServerConfigCenter();
        UniqueBeanManage.registerBean(configCenter);
        ServerTerminalSessionManager manager = new ServerTerminalSessionManager();

        EmbeddedChannel browser = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        manager.handleBrowserMessage(browser.pipeline().firstContext(), openRequest("session-a", "client-a"));

        TerminalWebSocketResponse response = readBrowserFrame(browser);
        assertEquals(TerminalWebSocketResponse.EVENT_ERROR, response.getEvent());
        assertEquals("目标设备不在线", response.getMessage());
    }

    @Test
    public void shouldRejectClientWithoutFullAuthorization() throws Exception {
        NDCServerConfigCenter configCenter = new NDCServerConfigCenter();
        UniqueBeanManage.registerBean(configCenter);
        ServerTerminalSessionManager manager = new ServerTerminalSessionManager();

        registerHolder(configCenter, holder("client-a", 0));

        EmbeddedChannel browser = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        manager.handleBrowserMessage(browser.pipeline().firstContext(), openRequest("session-a", "client-a"));

        TerminalWebSocketResponse response = readBrowserFrame(browser);
        assertEquals(TerminalWebSocketResponse.EVENT_ERROR, response.getEvent());
        assertEquals("目标设备未开启 FULL_AUTHORIZED", response.getMessage());
    }

    @Test
    public void shouldRejectSecondSessionForSameClient() throws Exception {
        NDCServerConfigCenter configCenter = new NDCServerConfigCenter();
        UniqueBeanManage.registerBean(configCenter);
        ServerTerminalSessionManager manager = new ServerTerminalSessionManager();

        ChannelHandlerContextHolder holder = holder("client-a", 1);
        EmbeddedChannel clientTunnel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        holder.setChannelHandlerContext(clientTunnel.pipeline().firstContext());
        registerHolder(configCenter, holder);

        EmbeddedChannel browser1 = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        manager.handleBrowserMessage(browser1.pipeline().firstContext(), openRequest("session-a", "client-a"));

        NDCMessageProtocol openProtocol = clientTunnel.readOutbound();
        assertNotNull(openProtocol);
        assertEquals(NDCMessageProtocol.TERMINAL_CONTROL, openProtocol.getType());
        assertEquals(TerminalControlMessage.ACTION_OPEN, openProtocol.getObject(TerminalControlMessage.class).getAction());

        EmbeddedChannel browser2 = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        manager.handleBrowserMessage(browser2.pipeline().firstContext(), openRequest("session-b", "client-a"));

        TerminalWebSocketResponse response = readBrowserFrame(browser2);
        assertEquals(TerminalWebSocketResponse.EVENT_ERROR, response.getEvent());
        assertTrue(response.getMessage().contains("已有活动终端会话"));
    }

    @Test
    public void shouldCleanupSessionWhenClientGoesOffline() throws Exception {
        NDCServerConfigCenter configCenter = new NDCServerConfigCenter();
        UniqueBeanManage.registerBean(configCenter);
        ServerTerminalSessionManager manager = new ServerTerminalSessionManager();

        ChannelHandlerContextHolder holder = holder("client-a", 1);
        EmbeddedChannel clientTunnel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        holder.setChannelHandlerContext(clientTunnel.pipeline().firstContext());
        registerHolder(configCenter, holder);

        EmbeddedChannel browser1 = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        manager.handleBrowserMessage(browser1.pipeline().firstContext(), openRequest("session-a", "client-a"));
        clientTunnel.readOutbound();

        manager.handleClientOffline("client-a");

        TerminalWebSocketResponse offlineResponse = readBrowserFrame(browser1);
        assertEquals(TerminalWebSocketResponse.EVENT_ERROR, offlineResponse.getEvent());
        assertEquals("设备离线，终端会话已关闭", offlineResponse.getMessage());

        EmbeddedChannel browser2 = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        manager.handleBrowserMessage(browser2.pipeline().firstContext(), openRequest("session-b", "client-a"));

        NDCMessageProtocol secondOpen = clientTunnel.readOutbound();
        assertNotNull(secondOpen);
        assertEquals(TerminalControlMessage.ACTION_OPEN, secondOpen.getObject(TerminalControlMessage.class).getAction());
    }

    private ChannelHandlerContextHolder holder(String clientId, int authMode) {
        ChannelHandlerContextHolder holder = new ChannelHandlerContextHolder(clientId);
        holder.setClientId(clientId);
        holder.setAuthMode(authMode);
        holder.setContextIp("127.0.0.1");
        holder.setContextPort(1081);
        return holder;
    }

    private TerminalWebSocketRequest openRequest(String sessionId, String clientId) {
        TerminalWebSocketRequest request = new TerminalWebSocketRequest();
        request.setEvent(TerminalWebSocketRequest.EVENT_OPEN);
        request.setSessionId(sessionId);
        request.setClientId(clientId);
        request.setCols(120);
        request.setRows(40);
        return request;
    }

    private TerminalWebSocketResponse readBrowserFrame(EmbeddedChannel browser) {
        TextWebSocketFrame frame = browser.readOutbound();
        return JSONUtils.str2Object(frame.text(), TerminalWebSocketResponse.class);
    }

    private void registerHolder(NDCServerConfigCenter configCenter, ChannelHandlerContextHolder holder) throws Exception {
        Field field = NDCServerConfigCenter.class.getDeclaredField("channelHandlerContextHolderMap");
        field.setAccessible(true);
        Map<String, ChannelHandlerContextHolder> holderMap = new ConcurrentHashMap<>();
        holderMap.put(holder.getClientId(), holder);
        field.set(configCenter, holderMap);
    }

    private void clearBeanMap() throws Exception {
        Field field = UniqueBeanManage.class.getDeclaredField("map");
        field.setAccessible(true);
        Map map = (Map) field.get(null);
        map.clear();
    }
}
