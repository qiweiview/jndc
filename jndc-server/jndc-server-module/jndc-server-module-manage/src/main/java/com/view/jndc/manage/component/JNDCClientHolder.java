package com.view.jndc.manage.component;

import com.view.core.client.ndc.NDCClient;
import com.view.core.client.ndc.NDCClientConfiguration;
import com.view.core.model.VirtualTCPService;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;
import com.view.jndc.manage.dao.jndc_client.JndcClientDao;
import com.view.jndc.manage.dao.jndc_client_service.JndcClientServiceDao;
import com.view.jndc.manage.enums.JNDCClientServiceStatusEnum;
import com.view.jndc.manage.enums.JNDCClientStatusEnum;
import com.view.jndc.manage.model.jndc_client.dto.JndcClientDTO;
import com.view.jndc.manage.model.jndc_client_service.d_o.JndcClientServiceDO;
import com.view.jndc.manage.model.jndc_client_service.dto.JndcClientServiceDTO;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.serviceI.jndc_client_service.JndcClientServiceServiceI;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    //处理循环依赖
    @Autowired
    @Lazy
    private  JndcClientServiceServiceI jndcClientServiceServiceI;

    private final JndcClientDao jndcClientDao;

    private final JndcClientServiceDao jndcClientServiceDao;

    public void startClient(JndcClientDTO jndcClientDTO) {
        String uniqueId = jndcClientDTO.getUniqueId();
        Long id = jndcClientDTO.getId();

        if (clientMap.containsKey(uniqueId)) {
            jndcClientDao.updateStatus(id, JNDCClientStatusEnum.CONNECT.value);
            throw new BizException("客户端已启动");
        }

        executorService.submit(() -> {


            NDCClientConfiguration ndcClientConfiguration = new NDCClientConfiguration();
            ndcClientConfiguration.setServerHost(jndcClientDTO.getServerHost());
            ndcClientConfiguration.setServerPort(jndcClientDTO.getServerPort());
            Integer reconnectInterval = jndcClientDTO.getReconnectInterval();
            ndcClientConfiguration.setReconnectInterval(reconnectInterval);
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

            ndcClientConfiguration.setProcessingCallback(() -> {
                JndcLogDTO jndcLogDTO = new JndcLogDTO();
                jndcLogDTO.setLogContent("等待" + reconnectInterval + "秒间隔后重试...");
                jndcLogDTO.setLogTime(LocalDateTime.now());
                jndcLogDTO.setLogType("client");
                jndcLogDTO.setSourceId(id);
                DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
                jndcLogServiceI.save(jndcLogDTO);
                jndcClientDao.updateStatus(id, JNDCClientStatusEnum.PROCESSING.value);
            });

            NDCClient ndcClient = new NDCClient();
            clientMap.put(uniqueId, ndcClient);


            try {
                //处理自动注册逻辑
                List<JndcClientServiceDO> clientServices = jndcClientDTO
                        .getClientServices();
                clientServices.stream()
                        .filter(JndcClientServiceDO::checkAutoRegister)//筛选自动注册的服务
                        .forEach(x->{
                            //todo 修改状态
                            Long id1 = x.getId();
                            JndcClientServiceDTO jndcClientServiceDTO = jndcClientServiceServiceI.getById(id1);
                            jndcClientServiceDTO.setServiceStatus(JNDCClientServiceStatusEnum.REGISTER.value);
                            //复用逻辑，存在重复查询，由于次数较少，暂不优化
                            jndcClientServiceServiceI.updateById(jndcClientServiceDTO);

                            JndcLogDTO jndcLogDTO = new JndcLogDTO();
                            jndcLogDTO.setLogContent("注册服务：" + x.getServiceName());
                            jndcLogDTO.setLogTime(LocalDateTime.now());
                            jndcLogDTO.setLogType("client");
                            jndcLogDTO.setSourceId(id);
                            DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
                            jndcLogServiceI.save(jndcLogDTO);
                        });
            } catch (Exception e) {
                log.error("自动注册失败", e);
            }

            try {
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
            log.warn("客户端:{}未启动,map中有{}个客户端", uniqueId, clientMap.size());
        } else {
            ndcClient.stop();
            clientMap.remove(uniqueId);
            Long id = dbData.getId();

            //更新状态
            DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
            jndcClientDao.updateStatus(id, JNDCClientStatusEnum.PAUSE.value);

            //设置所有服务为未注册
            jndcClientServiceDao.updateStatus(id, JNDCClientServiceStatusEnum.UN_REGISTER.value);
        }
    }

    public void registerService(String clientUniqueId, JndcClientServiceDTO jndcClientServiceDTO) {
        NDCClient ndcClient = clientMap.get(clientUniqueId);
        if (ndcClient==null){
            log.warn("客户端:{}未启动,map中有{}个客户端", clientUniqueId, clientMap.size());
        }else {
            //todo 发起注册
            VirtualTCPService virtualTCPService = new VirtualTCPService();
            virtualTCPService.setServiceId(jndcClientServiceDTO.getServiceUniqueId());
            virtualTCPService.setDescription(jndcClientServiceDTO.getServiceName());
            virtualTCPService.setHost(jndcClientServiceDTO.getServiceHost());
            virtualTCPService.setPort(jndcClientServiceDTO.getServicePort());
            virtualTCPService.setExpectPort(jndcClientServiceDTO.getExpectPort());
            ndcClient.registerService(virtualTCPService);
        }

    }

    public void unRegisterService(String uniqueId, JndcClientServiceDTO jndcClientServiceDTO) {
        NDCClient ndcClient = clientMap.get(uniqueId);
        if (ndcClient==null){
            log.warn("客户端:{}未启动,map中有{}个客户端", uniqueId, clientMap.size());
        }else {
            //todo 发起取消注册
            VirtualTCPService virtualTCPService = new VirtualTCPService();
            virtualTCPService.setServiceId(jndcClientServiceDTO.getServiceUniqueId());
            virtualTCPService.setDescription(jndcClientServiceDTO.getServiceName());
            virtualTCPService.setHost(jndcClientServiceDTO.getServiceHost());
            virtualTCPService.setPort(jndcClientServiceDTO.getServicePort());
            virtualTCPService.setExpectPort(jndcClientServiceDTO.getExpectPort());
            ndcClient.unRegisterService(virtualTCPService);
        }
    }
}
