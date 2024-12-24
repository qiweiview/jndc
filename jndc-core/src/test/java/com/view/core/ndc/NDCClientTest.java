package com.view.core.ndc;

import com.view.core.client.ndc.NDCClient;
import com.view.core.client.ndc.NDCClientConfiguration;
import com.view.core.component.SupportEnvironment;
import com.view.core.component.app_center.ServiceIdManager;
import com.view.core.model.VirtualTCPService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j

public class NDCClientTest {

    private NDCClient ndcClient;

    @BeforeEach
    public void init() {
        ndcClient = new NDCClient();
    }

    @Test
    public void runClient() {
        ServiceIdManager serviceIdManager = new ServiceIdManager();


        NDCClientConfiguration ndcClientConfiguration = new NDCClientConfiguration();
        ndcClientConfiguration.setServerHost("127.0.0.1");
        ndcClientConfiguration.setServerPort(8888);
        ndcClientConfiguration.setReconnectInterval(3);

        log.info("---准备发起注册---");

        String host = "qw607.com";
        int port = 80;

        VirtualTCPService virtualTCPService = new VirtualTCPService();
        virtualTCPService.setServiceId(serviceIdManager.generateServiceId(host, port));
        virtualTCPService.setDescription("测试服务");
        virtualTCPService.setHost(host);
        virtualTCPService.setPort(port);
        virtualTCPService.setExpectPort(3307);
        ndcClient.registerService(virtualTCPService);

        log.info("访问 http://127.0.0.1:3307");

        String host2 = "121.4.103.198";
        int port2 = 22;
        VirtualTCPService virtualTCPService2 = new VirtualTCPService();
        virtualTCPService2.setServiceId(serviceIdManager.generateServiceId(host2, port2));
        virtualTCPService2.setDescription("测试服务");
        virtualTCPService2.setHost(host2);
        virtualTCPService2.setPort(port2);
        virtualTCPService2.setExpectPort(3308);
        ndcClient.registerService(virtualTCPService2);


        String host3 = "127.0.0.1";
        int port3 = 3306;
        VirtualTCPService virtualTCPService3 = new VirtualTCPService();
        virtualTCPService3.setServiceId(serviceIdManager.generateServiceId(host3, port3));
        virtualTCPService3.setDescription("测试服务");
        virtualTCPService3.setHost(host3);
        virtualTCPService3.setPort(port3);
        virtualTCPService3.setExpectPort(3309);
        ndcClient.registerService(virtualTCPService3);

        ndcClientConfiguration.setStartedCallback(() -> {
            log.info("客户端启动成功");
        });

        ndcClientConfiguration.setStopCallback(() -> {
            log.info("客户端停止成功");
        });

        ndcClientConfiguration.setFailCallback(e -> {
            log.info("客户端启动失败");
        });

        ndcClientConfiguration.setAutoReconnect(true);

        ndcClientConfiguration.setReconnectMaxTimes(-1);

        ndcClientConfiguration.setUniqueId("client1");

        //定义服务
        ndcClient.start(ndcClientConfiguration);
    }


}
