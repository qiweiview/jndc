package com.view.core.server;

import com.view.core.server.tcp.ByteServerHandler;
import com.view.core.utils.UniqueId;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class ControllableServer {

    private Map<String, ByteServerHandler> sessionMap = new ConcurrentHashMap<>();

    //用于channel关闭时关联服务
    private String ndcClientId;

    //归属哪个ndc服务端
    private String ndcServerId;

    //通道消息解析后获取id，通过id找到该服务器
    private String appServerId = UniqueId.generate();

    //归属哪个客户端应用
    private String clientServiceId;


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
