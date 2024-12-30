package com.view.jndc.manage.component.client;

import com.view.core.client.ndc.flow.ClientFlowSlot;
import com.view.core.client.ndc.flow.DesignedClientFlow;
import com.view.core.model.local_service.LocalService;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.jndc.manage.dao.jndc_client.JndcClientDao;
import com.view.jndc.manage.dao.jndc_client_service.JndcClientServiceDao;
import com.view.jndc.manage.enums.client.JNDCClientServiceStatusEnum;
import com.view.jndc.manage.enums.client.JNDCClientStatusEnum;
import com.view.jndc.manage.model.jndc_client_service.JndcClientServiceStructMapper;
import com.view.jndc.manage.model.jndc_client_service.d_o.JndcClientServiceDO;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientFlowSlot extends ClientFlowSlot {
    private final JndcClientDao jndcClientDao;

    private final JndcClientServiceDao jndcClientServiceDao;

    private final JndcLogServiceI jndcLogServiceI;


    @Lazy
    @Autowired
    private JNDCClientHolder jndcClientHolder;

    @Override
    public void ndcClientStart() {
        Long id = getLongIdGetter().get();
        jndcClientDao.updateStatus(id, JNDCClientStatusEnum.PROCESSING.value);
        JndcLogDTO jndcLogDTO = new JndcLogDTO();
        jndcLogDTO.setLogContent("开始启动");
        jndcLogDTO.setLogTime(LocalDateTime.now());
        jndcLogDTO.setLogType("client");
        jndcLogDTO.setSourceId(id);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcLogServiceI.save(jndcLogDTO);
    }

    @Override
    public void ndcClientStartFail(Exception e) {
        Long id = getLongIdGetter().get();
        jndcClientDao.updateStatus(id, JNDCClientStatusEnum.PROCESSING.value);
        JndcLogDTO jndcLogDTO = new JndcLogDTO();
        jndcLogDTO.setLogContent("启动失败");
        jndcLogDTO.setLogTime(LocalDateTime.now());
        jndcLogDTO.setLogType("client");
        jndcLogDTO.setSourceId(id);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcLogServiceI.save(jndcLogDTO);
    }

    @Override
    protected void connectionActive() {
        Long id = getLongIdGetter().get();
        jndcClientDao.updateStatus(id, JNDCClientStatusEnum.CONNECT.value);

        JndcLogDTO jndcLogDTO = new JndcLogDTO();
        jndcLogDTO.setLogContent("建立连接成功");
        jndcLogDTO.setLogTime(LocalDateTime.now());
        jndcLogDTO.setLogType("client");
        jndcLogDTO.setSourceId(id);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcLogServiceI.save(jndcLogDTO);


    }

    @Override
    public void openChannel() {
        Long id = getLongIdGetter().get();
        jndcClientDao.updateStatus(id, JNDCClientStatusEnum.CONNECT.value);

        String clientId = getClientIdGetter().get();

        //自动注册
        List<JndcClientServiceDO> jndcClientServiceDOS = jndcClientServiceDao.listByClientId(id);
        jndcClientServiceDOS.parallelStream()
                .filter(x -> x.checkAutoRegister())
                .forEach(x -> {
                    LocalService localService = jndcClientHolder.serviceDTOToLocalService(JndcClientServiceStructMapper.INSTANCE.toDTO(x));
                    localService.setNdcClientId(clientId);
                    registerServiceManual(localService, true);
                });


        JndcLogDTO jndcLogDTO = new JndcLogDTO();
        jndcLogDTO.setLogContent("打开通道成功");
        jndcLogDTO.setLogTime(LocalDateTime.now());
        jndcLogDTO.setLogType("client");
        jndcLogDTO.setSourceId(id);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcLogServiceI.save(jndcLogDTO);
    }

    @Override
    public void registerTCPService(String serviceId) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientServiceDao.updateStatus(serviceId, JNDCClientServiceStatusEnum.REGISTER.value);
    }

    @Override
    public void unregisterTCPService(String serviceId) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientServiceDao.updateStatus(serviceId, JNDCClientServiceStatusEnum.UN_REGISTER.value);
    }

    @Override
    public void tcpClientStart(String serviceId, String tcpChannelId) {

    }

    @Override
    public void tcpChannelActive(String serviceId, String tcpChannelId) {

    }

    @Override
    public void tcpClientStartFail(String serviceId, String tcpChannelId) {

    }

    @Override
    public void tcpChannelRead(String serviceId, String tcpChannelId, byte[] bytes) {

    }

    @Override
    public void tcpChannelInactive(String serviceId, String tcpChannelId) {

    }

    @Override
    public void tcpChannelWrite(String serviceId, String tcpChannelId, byte[] bytes) {

    }

    @Override
    public void ndcClientInActive() {
        Long id = getLongIdGetter().get();
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientDao.updateStatus(id, JNDCClientStatusEnum.PROCESSING.value);

        JndcLogDTO jndcLogDTO = new JndcLogDTO();
        jndcLogDTO.setLogContent("连接中断");
        jndcLogDTO.setLogTime(LocalDateTime.now());
        jndcLogDTO.setLogType("client");
        jndcLogDTO.setSourceId(id);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcLogServiceI.save(jndcLogDTO);
    }

    @Override
    public void ndcClientStop() {
        String uniqueId = getClientIdGetter().get();
        Long id = getLongIdGetter().get();
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientDao.updateStatus(id, JNDCClientStatusEnum.PAUSE.value);
        Map<String, DesignedClientFlow> clientMap = jndcClientHolder.getClientMap();
        DesignedClientFlow remove = clientMap.remove(uniqueId);
        if (remove == null) {
            log.warn("client not found:{}", uniqueId);
        } else {
            JndcLogDTO jndcLogDTO = new JndcLogDTO();
            jndcLogDTO.setLogContent("客户端停止");
            jndcLogDTO.setLogTime(LocalDateTime.now());
            jndcLogDTO.setLogType("client");
            jndcLogDTO.setSourceId(id);
            DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
            jndcLogServiceI.save(jndcLogDTO);
        }


    }
}
