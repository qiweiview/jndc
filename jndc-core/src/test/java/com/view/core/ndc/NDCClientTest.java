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
        String generate = UniqueId.generate();
        log.info("生成的服务id为：{}", generate);
        virtualTCPService.setServiceId(generate);
        virtualTCPService.setDescription("测试服务");
        virtualTCPService.setHost("qw607.com");
        virtualTCPService.setPort(80);
        virtualTCPService.setExpectPort(3307);
        ndcClient.registerService(virtualTCPService);

        //定义服务
        ndcClient.start(ndcClientConfiguration);
    }


}
