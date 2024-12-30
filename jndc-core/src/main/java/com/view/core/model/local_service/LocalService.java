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

    private long connectTimeout;

    private String host;

    private int port;

    private int expectBindPort;

    private String ndcClientId;

    private String serviceId;

    private String name;

    private RegisterResponse registerResponse;

    private TCPServerConfiguration tcpServerConfiguration;

    //不序列化
    private transient Map<String, TCPClient> tcpClientMap;

    public boolean isSuccessful() {
        nullWarning();
        return RegisterResponse.SUCCESS.equals(registerResponse);
    }

    public boolean isServiceNotExist() {
        nullWarning();
        return RegisterResponse.SERVICE_NOT_EXIST.equals(registerResponse);
    }

    public boolean isTCPServerStartFail() {
        nullWarning();
        return RegisterResponse.TCP_SERVER_START_FAIL.equals(registerResponse);
    }

    public boolean isServiceExist() {
        nullWarning();
        return RegisterResponse.SERVICE_EXIST.equals(registerResponse);
    }

    public boolean isPortHasBound() {
        nullWarning();
        return RegisterResponse.PORT_HAS_BOUND.equals(registerResponse);
    }

    public boolean isClientNotExist() {
        nullWarning();
        return RegisterResponse.CLIENT_NOT_EXIST.equals(registerResponse);
    }

    public void nullWarning() {
        if (registerResponse == null) {
            log.warn("registerResponse is null");
        }
    }


    public void stop() {
        //关闭所有客户端
        tcpClientMap.forEach((s, tcpClient) -> {
            tcpClient.stop();
        });
    }

    public void initTCPClientMap() {
        tcpClientMap = new ConcurrentHashMap<>();
    }
}
