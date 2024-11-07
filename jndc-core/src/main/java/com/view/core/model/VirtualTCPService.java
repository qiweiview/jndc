package com.view.core.model;

import com.view.core.client.ControllableClient;
import com.view.core.client.tcp.TCPClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class VirtualTCPService implements Serializable {
    //由上下文填写
    private String ndcClientId;


    /*------本地服务------*/
    private int expectPort;

    private String serviceId;

    private String description;

    private String host;

    private int port;

    //key:远程会话id
    private Map<String, ControllableClient> controllableClientMap = new HashMap<>();
    /*------本地服务------*/


    /**
     * 为远程会话创建客户端
     *
     * @param tcpDataTransport 远程会话信息
     */
    public void createClientForRemoteSession(TCPDataTransport tcpDataTransport) {
        new Thread(() -> {
            String appServerSessionId = tcpDataTransport.getAppServerSessionId();
            String appServerId = tcpDataTransport.getAppServerId();

            //todo 创建客户端
            TCPClient tcpClient = new TCPClient();
            tcpClient.setAppServerId(appServerId);
            tcpClient.setAppServerSessionId(appServerSessionId);
            tcpClient.setClientServiceId(serviceId);
            tcpClient.start(host, port);

            //注册客户端
            controllableClientMap.put(appServerSessionId, tcpClient);
        }).start();
        log.info("为远程会话创建客户端：{}", tcpDataTransport.getAppServerSessionId());
    }

    public void receiveDataFromRemoteSession(TCPDataTransport tcpDataTransport) {
        ControllableClient controllableClient = controllableClientMap.get(tcpDataTransport.getAppServerSessionId());
        if (controllableClient != null) {
            //todo 接收数据
        }

    }
}
