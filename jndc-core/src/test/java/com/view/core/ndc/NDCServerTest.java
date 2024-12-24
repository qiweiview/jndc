package com.view.core.ndc;

import com.view.core.server.ndc.NDCServer;
import com.view.core.server.ndc.NDCServerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NDCServerTest {

    private NDCServer ndcServer;

    @BeforeEach
    public void init() {
        ndcServer = new NDCServer();
    }

    @Test
    public void runServer() {
        NDCServerConfiguration ndcServerConfiguration = new NDCServerConfiguration();
        ndcServerConfiguration.setHost("127.0.0.1");
        ndcServerConfiguration.setPort(8888);
        ndcServerConfiguration.setUniqueId("server1");
        ndcServerConfiguration.setStartedCallback(() -> {
            System.out.println("服务启动成功");
        });
        ndcServerConfiguration.setStopCallback(() -> {
            System.out.println("服务停止成功");
        });
        ndcServerConfiguration.setFailCallback(e -> {
            System.out.println("服务启动失败");
        });
        ndcServerConfiguration.setConnectActiveCallback((e) -> {
            System.out.println("连接激活");
            return e;
        });
        ndcServerConfiguration.setConnectInActiveCallback((e) -> {
            System.out.println("连接失活");
        });


        ndcServer.start(ndcServerConfiguration);
    }
}
