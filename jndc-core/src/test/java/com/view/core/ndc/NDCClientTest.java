package com.view.core.ndc;

import com.view.core.client.ndc.NDCClientConfiguration;
import com.view.core.client.ndc.flow.ClientFlowSlot;
import com.view.core.client.ndc.flow.DesignedClientFlow;
import com.view.core.client.ndc.flow.EmptyClientFlowSlot;
import com.view.core.component.app_center.ServiceIdManager;
import com.view.core.model.local_service.LocalService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j

public class NDCClientTest {


    @Test
    public void runClient() {

        ServiceIdManager serviceIdManager = new ServiceIdManager();

        String clientId = "client1";
        NDCClientConfiguration ndcClientConfiguration = new NDCClientConfiguration();
        ndcClientConfiguration.setServerHost("127.0.0.1");//服务端地址
        ndcClientConfiguration.setServerPort(9866);//服务端端口
        ndcClientConfiguration.setReconnectInterval(3);//重连间隔
        ndcClientConfiguration.setAutoReconnect(true);//自动重连
        ndcClientConfiguration.setReconnectMaxTimes(-1);//不限制最大重连次数
        ndcClientConfiguration.setUniqueId(clientId);//客户端唯一标识

        log.info("---准备发起注册---");

        //定义暴露服务
        String host2 = "github.com";
        int port2 = 443;
        LocalService localService2 = new LocalService();
        localService2.setServiceId(serviceIdManager.generateServiceId(host2, port2));
        localService2.setName("github.com");
        localService2.setHost(host2);
        localService2.setPort(port2);
        localService2.setExpectBindPort(1443);
        localService2.setNdcClientId(clientId);
        localService2.setConnectTimeout(30 * 1000);


        ClientFlowSlot emptyClientFlowSlot = new EmptyClientFlowSlot();
        new Thread(() -> {
            //注册服务
            emptyClientFlowSlot.registerServiceManual(localService2, true);
        }).start();

        DesignedClientFlow designedClientFlow = new DesignedClientFlow(ndcClientConfiguration, emptyClientFlowSlot);
        designedClientFlow.run();

    }


}
