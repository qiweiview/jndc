package com.view.core.ndc;

import com.view.core.client.ndc.NDCClient;
import com.view.core.client.ndc.NDCClientConfiguration;
import com.view.core.model.VirtualTCPService;
import com.view.core.utils.UniqueId;
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


        NDCClientConfiguration ndcClientConfiguration = new NDCClientConfiguration();
        ndcClientConfiguration.setHost("127.0.0.1");
        ndcClientConfiguration.setPort(10886);
        ndcClientConfiguration.setTimeoutSecond(3);

        log.info("---准备发起注册---");


        VirtualTCPService virtualTCPService = new VirtualTCPService();
        virtualTCPService.setServiceId(UniqueId.generate());
        virtualTCPService.setDescription("测试服务");
        virtualTCPService.setHost("qw607.com");
        virtualTCPService.setPort(80);
        virtualTCPService.setExpectPort(3307);
        ndcClient.registerService(virtualTCPService);

        VirtualTCPService virtualTCPService2 = new VirtualTCPService();
        virtualTCPService2.setServiceId(UniqueId.generate());
        virtualTCPService2.setDescription("测试服务");
        virtualTCPService2.setHost("121.4.103.198");
        virtualTCPService2.setPort(22);
        virtualTCPService2.setExpectPort(3308);
        ndcClient.registerService(virtualTCPService2);


        VirtualTCPService virtualTCPService3 = new VirtualTCPService();
        virtualTCPService3.setServiceId(UniqueId.generate());
        virtualTCPService3.setDescription("测试服务");
        virtualTCPService3.setHost("127.0.0.1");
        virtualTCPService3.setPort(3306);
        virtualTCPService3.setExpectPort(3309);
        ndcClient.registerService(virtualTCPService3);

        //定义服务
        ndcClient.start(ndcClientConfiguration);
    }


}
