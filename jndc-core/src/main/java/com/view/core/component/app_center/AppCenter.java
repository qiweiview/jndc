package com.view.core.component.app_center;

import com.view.core.model.VirtualService;
import com.view.core.server.http.HttpServer;
import com.view.core.server.tcp.TCPServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AppCenter {
    private Map<String, HttpServer> httpServerMap = new ConcurrentHashMap<>();
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
     * @param virtualService
     */
    public void deployService(VirtualService virtualService) {
        int expectPort = virtualService.getExpectPort();
        if (portBindable(expectPort)) {
            //todo 可以绑定
            TCPServer tcpServer = new TCPServer();
            tcpServer.start(expectPort);
            tcpServerMap.put(virtualService.getServiceId(), tcpServer);
            log.info("服务部署成功：{}", virtualService);
        } else {
            //todo 端口不可绑定
            log.error("服务部署失败：期望端口{}不可用", expectPort);
        }

    }
}
