package com.view.core.model;

import com.view.core.server.tcp.TCPServer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通道打开
 */
@Data
@Slf4j
public class ChannelOpen implements Serializable {
    public static final long serialVersionUID = -4599902495744735536L;

    private String ndcClientId;

    private Map<String, TCPServer> tcpServerMap = new ConcurrentHashMap<>();
}
