package com.view.jndc.manage.component.server;

import com.view.core.server.ndc.NDCServerConfiguration;
import com.view.core.server.ndc.flow.DesignedServerFlow;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Component
@Slf4j
public class JNDCServerHolder {
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private Map<String, DesignedServerFlow> serverMap = new ConcurrentHashMap<>();

    private final WebServerFlowSlot webServerFlowSlot;


    public void startServer(JndcServerDTO jndcServerDTO) {
        executorService.submit(() -> {
            NDCServerConfiguration ndcServerConfiguration = new NDCServerConfiguration();
            ndcServerConfiguration.setHost(jndcServerDTO.getBindHost());
            ndcServerConfiguration.setPort(jndcServerDTO.getBindPort());
            ndcServerConfiguration.setUniqueId(jndcServerDTO.getUniqueId());


            //流程
            DesignedServerFlow designedServerFlow = new DesignedServerFlow(ndcServerConfiguration, webServerFlowSlot);
            designedServerFlow.setLongId(jndcServerDTO.getId());

            serverMap.put(jndcServerDTO.getUniqueId(), designedServerFlow);
            designedServerFlow.run();
        });
    }


    public void stopServer(JndcServerDTO jndcServerDTO) {
        DesignedServerFlow designedServerFlow = serverMap.get(jndcServerDTO.getUniqueId());
        if (designedServerFlow != null) {
            designedServerFlow.stop();
            serverMap.remove(jndcServerDTO.getUniqueId());
        }

    }
}
