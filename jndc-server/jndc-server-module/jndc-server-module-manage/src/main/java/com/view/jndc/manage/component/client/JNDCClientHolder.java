package com.view.jndc.manage.component.client;

import com.view.core.client.ndc.NDCClientConfiguration;
import com.view.core.client.ndc.flow.DesignedClientFlow;
import com.view.core.model.local_service.LocalService;
import com.view.jndc.manage.model.jndc_client.dto.JndcClientDTO;
import com.view.jndc.manage.model.jndc_client_service.dto.JndcClientServiceDTO;
import lombok.Data;
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
@Data
public class JNDCClientHolder {
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private Map<String, DesignedClientFlow> clientMap = new ConcurrentHashMap<>();

    private final WebClientFlowSlot webClientFlowSlot;


    public void startClient(JndcClientDTO jndcClientDTO) {
        executorService.submit(() -> {
            NDCClientConfiguration ndcClientConfiguration = new NDCClientConfiguration();
            ndcClientConfiguration.setServerHost(jndcClientDTO.getServerHost());//服务端地址
            ndcClientConfiguration.setServerPort(jndcClientDTO.getServerPort());//服务端端口
            ndcClientConfiguration.setReconnectInterval(jndcClientDTO.getReconnectInterval());//重连间隔
            ndcClientConfiguration.setAutoReconnect(jndcClientDTO.getAutoReconnect() == 1);//自动重连
            ndcClientConfiguration.setReconnectMaxTimes(jndcClientDTO.getReconnectMaxTimes());//不限制最大重连次数
            ndcClientConfiguration.setUniqueId(jndcClientDTO.getUniqueId());//客户端唯一标识


            //注册服务
            DesignedClientFlow designedClientFlow = new DesignedClientFlow(ndcClientConfiguration, webClientFlowSlot);
            designedClientFlow.setLongId(jndcClientDTO.getId());

            clientMap.put(jndcClientDTO.getUniqueId(), designedClientFlow);
            designedClientFlow.run();
        });
    }

    public void stopClient(JndcClientDTO dbData) {
        DesignedClientFlow designedClientFlow = clientMap.get(dbData.getUniqueId());
        if (designedClientFlow != null) {
            designedClientFlow.stop();
        } else {
            log.warn("client not found:{}", dbData.getUniqueId());
        }
    }

    public LocalService serviceDTOToLocalService(JndcClientServiceDTO jndcClientServiceDTO) {
        LocalService localService = new LocalService();
        localService.setServiceId(jndcClientServiceDTO.getServiceUniqueId());
        localService.setHost(jndcClientServiceDTO.getServiceHost());
        localService.setPort(jndcClientServiceDTO.getServicePort());
        localService.setName(jndcClientServiceDTO.getServiceName());
        localService.setExpectBindPort(jndcClientServiceDTO.getExpectPort());
        return localService;
    }

    public void registerService(String clientUniqueId, JndcClientServiceDTO jndcClientServiceDTO) {
        DesignedClientFlow designedClientFlow = clientMap.get(clientUniqueId);
        if (designedClientFlow != null) {
            Integer autoRegister = jndcClientServiceDTO.getAutoRegister();
            LocalService localService = serviceDTOToLocalService(jndcClientServiceDTO);
            localService.setNdcClientId(clientUniqueId);
            designedClientFlow.getClientFlowSlot().registerServiceManual(localService, autoRegister == 1);
        }
    }

    public void unRegisterService(String uniqueId, JndcClientServiceDTO jndcClientServiceDTO) {
        DesignedClientFlow designedClientFlow = clientMap.get(uniqueId);
        if (designedClientFlow != null) {
            LocalService localService = new LocalService();
            localService.setServiceId(jndcClientServiceDTO.getServiceUniqueId());

            designedClientFlow.getClientFlowSlot().unregisterServiceManual(localService, jndcClientServiceDTO.getAutoRegister() == 0);
        }
    }
}
