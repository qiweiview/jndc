package com.view.core.ndc;

import com.view.core.client.ndc.NDCClient;
import com.view.core.model.VirtualService;
import com.view.core.utils.UniqueId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NDCClientTest {

    private NDCClient ndcClient;

    @BeforeEach
    public void init() {
        ndcClient = new NDCClient();
    }

    @Test
    public void runClient() {
        VirtualService virtualService = new VirtualService();
        virtualService.setServiceId(UniqueId.generate());
        virtualService.setDescription("测试服务");
        virtualService.setHost("127.0.0.1");
        virtualService.setPort(3306);
        virtualService.setExpectPort(3307);
        ndcClient.registerService(virtualService);
        ndcClient.start("127.0.0.1", 10886);


    }
}
