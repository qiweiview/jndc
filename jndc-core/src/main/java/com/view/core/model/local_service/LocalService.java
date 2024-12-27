package com.view.core.model.local_service;

import com.view.core.client.tcp.TCPClient;
import com.view.core.server.tcp.TCPServerConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class LocalService implements Serializable {
    private static final long serialVersionUID = -5630844448490385143L;

    private String host;

    private int port;

    private int expectBindPort;

    private String ndcClientId;

    private String serviceId;

    private String name;

    private RegisterResponse registerResponse;

    private TCPServerConfiguration tcpServerConfiguration;

    //不序列化
    private transient Map<String, TCPClient> tcpClientMap = new ConcurrentHashMap<>();

    public boolean isSuccessful() {
        return RegisterResponse.SUCCESS.equals(registerResponse);
    }

    public boolean isServiceNotExist() {
        return RegisterResponse.SERVICE_NOT_EXIST.equals(registerResponse);
    }

    public boolean isOtherError() {
        return RegisterResponse.OTHER_ERROR.equals(registerResponse);
    }

    public boolean isServiceExist() {
        return RegisterResponse.SERVICE_EXIST.equals(registerResponse);
    }

    public boolean isPortHasBound() {
        return RegisterResponse.PORT_HAS_BOUND.equals(registerResponse);
    }


    public void stop() {
        //关闭所有客户端
        tcpClientMap.forEach((s, tcpClient) -> {
            tcpClient.stop();
        });
    }
}
