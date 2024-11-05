package com.view.core.ndc;

import com.view.core.client.ndc.NDCClient;
import com.view.core.model.VirtualService;
import com.view.core.utils.UniqueId;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@Slf4j

public class NDCClientTest {

    private NDCClient ndcClient;

    @BeforeEach
    public void init() {
        ndcClient = new NDCClient();
    }

    @Test
    public void runClient() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            log.info("---准备发起注册---");
            VirtualService virtualService = new VirtualService();
            virtualService.setServiceId(UniqueId.generate());
            virtualService.setDescription("测试服务");
            virtualService.setHost("127.0.0.1");
            virtualService.setPort(3306);
            virtualService.setExpectPort(3307);
            ndcClient.registerService(virtualService);
        }).start();

        //定义服务
        ndcClient.start("127.0.0.1", 10886);


    }
}
