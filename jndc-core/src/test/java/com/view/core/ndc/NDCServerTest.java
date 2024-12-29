package com.view.core.ndc;

import com.view.core.server.ndc.NDCServerConfiguration;
import com.view.core.server.ndc.flow.DesignedServerFlow;
import com.view.core.server.ndc.flow.EmptyServerFlowSlot;
import com.view.core.server.ndc.flow.ServerFlowSlot;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class NDCServerTest {



    @Test
    public void runServer() {
        NDCServerConfiguration ndcServerConfiguration = new NDCServerConfiguration();
        ndcServerConfiguration.setHost("127.0.0.1");
        ndcServerConfiguration.setPort(8888);
        ndcServerConfiguration.setUniqueId("server1");
        //事件插槽
        ServerFlowSlot serverFlowSlot = new EmptyServerFlowSlot();
        //流程
        DesignedServerFlow designedServerFlow = new DesignedServerFlow(ndcServerConfiguration, serverFlowSlot);
        designedServerFlow.run();
    }
}
