package com.view.core.server;

import com.view.core.server.tcp.ByteServerHandler;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class ControllableServer {

    private Map<String, ByteServerHandler> sessionMap = new ConcurrentHashMap<>();



    /**
     * 注册会话
     *
     * @param longText
     * @param byteServerHandler
     */
    public void registerSession(String longText, ByteServerHandler byteServerHandler) {
        sessionMap.put(longText, byteServerHandler);
    }

    public abstract void stop();
}
