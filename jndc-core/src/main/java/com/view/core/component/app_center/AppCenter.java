package com.view.core.component.app_center;

import com.view.core.component.SupportEnvironment;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.local_service.LocalService;
import com.view.core.server.tcp.TCPServer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
public class AppCenter {
    private SupportEnvironment supportEnvironment;

    //key:serviceId
    private Map<String, TCPServer> tcpServerMap = new ConcurrentHashMap<>();


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
     * @param localService
     */
    public void deployService(LocalService localService) {
        String ndcClientId = localService.getNdcClientId();
        String serviceId = localService.getServiceId();
        if (ndcClientId == null) {
            log.error("服务部署失败：clientId为空");
            return;
        }

        if (serviceId == null) {
            log.error("服务部署失败：serviceId为空");
            return;
        }


        int expectPort = localService.getExpectBindPort();
        if (portBindable(expectPort)) {
            //todo 可以绑定
            TCPServer tcpServer = new TCPServer(supportEnvironment);
            tcpServer.setDescription(localService.prettyDescription());
            tcpServer.setNdcClientId(ndcClientId);
            tcpServer.setClientServiceId(localService.getServiceId());
            tcpServer.start(expectPort, () -> {
                String serviceId1 = localService.getServiceId();
                tcpServerMap.put(serviceId1, tcpServer);
                log.info("tcp服务部署成功：服务id为{}的服务已部署", serviceId1);
            });
        } else {
            //todo 端口不可绑定
            log.error("服务部署失败：期望端口{}不可用", expectPort);
        }

    }

    public void withdrawService(LocalService localService) {
        String ndcClientId = localService.getNdcClientId();
        String serviceId = localService.getServiceId();
        if (ndcClientId == null) {
            log.error("服务部署失败：clientId为空");
            return;
        }

        if (serviceId == null) {
            log.error("服务部署失败：serviceId为空");
            return;
        }

        TCPServer tcpServer = tcpServerMap.get(serviceId);
        if (tcpServer == null) {
            log.warn("未找到{}对应的TCPServer服务",serviceId);
        } else {
            //todo 停止服务
            tcpServer.stop();
            tcpServerMap.remove(serviceId);
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
            log.warn("未找到{}对应的TCPServer服务",clientServiceId);
        } else {
            //todo 通知客户端已经就绪
            tcpServer.noticeActiveCompleted(tcpDataTransport);
        }
    }

    public void receiveData(TCPDataTransport tcpDataTransport) {
        String clientServiceId = tcpDataTransport.getClientServiceId();
        TCPServer tcpServer = tcpServerMap.get(clientServiceId);
        if (tcpServer == null) {
            log.warn("未找到{}对应的TCPServer服务",clientServiceId);
        } else {
            tcpServer.receiveData(tcpDataTransport);
        }
    }


}
