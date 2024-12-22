package com.view.jndc.manage.component;

import com.view.core.client.ndc.NDCClient;
import com.view.core.client.ndc.NDCClientConfiguration;
import com.view.core.model.VirtualTCPService;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.jndc.manage.dao.jndc_client.JndcClientDao;
import com.view.jndc.manage.enums.JNDCClientServiceStatusEnum;
import com.view.jndc.manage.enums.JNDCClientStatusEnum;
import com.view.jndc.manage.model.jndc_client.dto.JndcClientDTO;
import com.view.jndc.manage.model.jndc_client_service.d_o.JndcClientServiceDO;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Component
@Slf4j
public class JNDCClientHolder {
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private Map<String, NDCClient> clientMap = new ConcurrentHashMap<>();

    private final JndcLogServiceI jndcLogServiceI;

    private final JndcClientDao jndcClientDao;

    public void startClient(JndcClientDTO jndcClientDTO) {
        String uniqueId = jndcClientDTO.getUniqueId();
        Long id = jndcClientDTO.getId();

        executorService.submit(() -> {
            if (clientMap.containsKey(uniqueId)) {
                log.warn("客户端已经启动");
                jndcClientDao.updateStatus(id, JNDCClientStatusEnum.CONNECT.value);
                return;
            }

            NDCClientConfiguration ndcClientConfiguration = new NDCClientConfiguration();
            ndcClientConfiguration.setServerHost(jndcClientDTO.getServerHost());
            ndcClientConfiguration.setServerPort(jndcClientDTO.getServerPort());
            ndcClientConfiguration.setReconnectInterval(jndcClientDTO.getReconnectInterval());
            ndcClientConfiguration.setStartedCallback(() -> {
                JndcLogDTO jndcLogDTO = new JndcLogDTO();
                jndcLogDTO.setLogContent("客户端启动");
                jndcLogDTO.setLogTime(LocalDateTime.now());
                jndcLogDTO.setLogType("client");
                jndcLogDTO.setSourceId(id);
                DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
                jndcLogServiceI.save(jndcLogDTO);
                jndcClientDao.updateStatus(id, JNDCClientStatusEnum.CONNECT.value);
            });

            ndcClientConfiguration.setStopCallback(() -> {
                JndcLogDTO jndcLogDTO = new JndcLogDTO();
                jndcLogDTO.setLogContent("客户端停止");
                jndcLogDTO.setLogTime(LocalDateTime.now());
                jndcLogDTO.setLogType("client");
                jndcLogDTO.setSourceId(id);
                DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
                jndcLogServiceI.save(jndcLogDTO);
                jndcClientDao.updateStatus(id, JNDCClientStatusEnum.PAUSE.value);
            });

            ndcClientConfiguration.setFailCallback(e -> {
                JndcLogDTO jndcLogDTO = new JndcLogDTO();
                jndcLogDTO.setLogContent("客户端启动失败");
                jndcLogDTO.setLogTime(LocalDateTime.now());
                jndcLogDTO.setLogType("client");
                jndcLogDTO.setSourceId(id);
                DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
                jndcLogServiceI.save(jndcLogDTO);
                jndcClientDao.updateStatus(id, JNDCClientStatusEnum.PAUSE.value);
            });
            ndcClientConfiguration.setAutoReconnect(jndcClientDTO.autoRegister());
            ndcClientConfiguration.setReconnectMaxTimes(jndcClientDTO.getReconnectMaxTimes());
            ndcClientConfiguration.setUniqueId(jndcClientDTO.getUniqueId());


            NDCClient ndcClient = new NDCClient();
            List<JndcClientServiceDO> clientServices = jndcClientDTO
                    .getClientServices();


            if (clientServices != null) {
                //todo 注册服务
                clientServices.stream()
                        .filter(x -> JNDCClientServiceStatusEnum.REGISTER.value.equals(x.getServiceStatus()))
                        .forEach(x -> {
                            VirtualTCPService virtualTCPService = new VirtualTCPService();
                            virtualTCPService.setServiceId(x.getServiceUniqueId());
                            virtualTCPService.setDescription(x.getServiceName());
                            virtualTCPService.setHost(x.getServiceHost());
                            virtualTCPService.setPort(x.getServicePort());
                            virtualTCPService.setExpectPort(x.getExpectPort());
                            ndcClient.registerService(virtualTCPService);
                            JndcLogDTO jndcLogDTO = new JndcLogDTO();
                            jndcLogDTO.setLogContent("注册服务：" + x.getServiceName());
                            jndcLogDTO.setLogTime(LocalDateTime.now());
                            jndcLogDTO.setLogType("client");
                            jndcLogDTO.setSourceId(id);
                            DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
                            jndcLogServiceI.save(jndcLogDTO);
                        });
            }


            try {
                clientMap.put(uniqueId, ndcClient);

                DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
                jndcClientDao.updateStatus(id, JNDCClientStatusEnum.PROCESSING.value);
            } catch (Exception e) {
                log.error("参数更新失败", e);
            }

            try {
                ndcClient.start(ndcClientConfiguration);
            } catch (Exception e) {
                clientMap.remove(uniqueId);
            }
        });
    }

    public void stopClient(JndcClientDTO dbData) {
        String uniqueId = dbData.getUniqueId();
        NDCClient ndcClient = clientMap.get(uniqueId);
        if (ndcClient == null) {
            log.warn("客户端:{}未启动", uniqueId);
        } else {
            ndcClient.stop();
            clientMap.remove(uniqueId);
            Long id = dbData.getId();

            DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
            jndcClientDao.updateStatus(id, JNDCClientStatusEnum.PAUSE.value);
        }
    }
}
