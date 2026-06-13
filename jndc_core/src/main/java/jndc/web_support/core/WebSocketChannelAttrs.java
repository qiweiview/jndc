package jndc.web_support.core;

import io.netty.util.AttributeKey;

public class WebSocketChannelAttrs {

    public static final AttributeKey<String> MODE = AttributeKey.valueOf("jndc.websocket.mode");

    private WebSocketChannelAttrs() {
    }
}
