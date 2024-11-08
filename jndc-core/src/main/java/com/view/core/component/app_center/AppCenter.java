package com.view.core.component.app_center;

import com.view.core.model.TCPDataTransport;
import com.view.core.model.VirtualTCPService;
import com.view.core.server.tcp.TCPServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

@Slf4j
public class AppCenter {
    //key:serviceId
    private Map<String, TCPServer> tcpServerMap = new ConcurrentHashMap<>();


    public AppCenter() {
        initHealthyChecker();
    }

    private void initHealthyChecker() {

        //每分钟检查一次
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            tcpServerMap.forEach((k, server) -> {
                //todo 检查服务是否健康
                server.checkHealthy();
            });
        }, 0, 1, java.util.concurrent.TimeUnit.MINUTES);
        log.info("健康检查器已启动，频率为一分钟一次");
    }

    public static boolean portBindable(int port) {
        //todo 检查本地端口是否被占用
        try (ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName("0.0.0.0"))) {
            //todo 端口可绑定
            return true;
        } catch (Exception e) {
            //todo 端口不可绑定
            return false;
        }
    }

    /**
     * 部署服务
     *
     * @param virtualTCPService
     */
    public void deployService(VirtualTCPService virtualTCPService) {
        String ndcClientId = virtualTCPService.getNdcClientId();
        String serviceId = virtualTCPService.getServiceId();
        if (ndcClientId == null) {
            log.error("服务部署失败：clientId为空");
            return;
        }

        if (serviceId == null) {
            log.error("服务部署失败：serviceId为空");
            return;
        }


        int expectPort = virtualTCPService.getExpectPort();
        if (portBindable(expectPort)) {
            //todo 可以绑定
            TCPServer tcpServer = new TCPServer();
            tcpServer.setNdcClientId(ndcClientId);
            tcpServer.setClientServiceId(virtualTCPService.getServiceId());
            tcpServer.start(expectPort, () -> {
                String serviceId1 = virtualTCPService.getServiceId();
                tcpServerMap.put(serviceId1, tcpServer);
                log.info("tcp服务部署成功：服务id为{}的服务已部署", serviceId1);
            });
        } else {
            //todo 端口不可绑定
            log.error("服务部署失败：期望端口{}不可用", expectPort);
        }

    }


    public void withdrawRelationalService(String clientId) {
        tcpServerMap.forEach((k, tcpServer) -> {
            if (tcpServer.getNdcClientId().equals(clientId)) {
                tcpServer.stop();
                tcpServerMap.remove(k);
            }
        });
    }

    public void noticeActiveCompleted(TCPDataTransport tcpDataTransport) {
        String clientServiceId = tcpDataTransport.getClientServiceId();
        TCPServer tcpServer = tcpServerMap.get(clientServiceId);
        if (tcpServer == null) {
            log.warn("未找到对应的服务");
        } else {
            //todo 通知客户端已经就绪
            tcpServer.noticeActiveCompleted(tcpDataTransport);
        }
    }

    public void receiveData(TCPDataTransport tcpDataTransport) {
        String clientServiceId = tcpDataTransport.getClientServiceId();
        TCPServer tcpServer = tcpServerMap.get(clientServiceId);
        if (tcpServer == null) {
            log.warn("未找到对应的服务");
        } else {
            tcpServer.receiveData(tcpDataTransport);
        }
    }
}
