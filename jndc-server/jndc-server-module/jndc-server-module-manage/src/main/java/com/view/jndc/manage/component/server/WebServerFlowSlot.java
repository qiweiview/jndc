package com.view.jndc.manage.component.server;

import com.view.core.server.ndc.flow.ServerFlowSlot;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.jndc.manage.dao.jndc_server.JndcServerDao;
import com.view.jndc.manage.enums.JNDCServerStatusEnum;
import com.view.jndc.manage.model.jndc_access_history.dto.JndcAccessHistoryDTO;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.model.jndc_server_accept_history.dto.JndcServerAcceptHistoryDTO;
import com.view.jndc.manage.serviceI.jndc_access_history.JndcAccessHistoryServiceI;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import com.view.jndc.manage.serviceI.jndc_server_accept_history.JndcServerAcceptHistoryServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebServerFlowSlot extends ServerFlowSlot {
    private final JndcServerDao jndcServerDao;

    private final JndcServerAcceptHistoryServiceI jndcServerAcceptHistoryServiceI;

    private final JndcLogServiceI jndcLogServiceI;

    private final JndcAccessHistoryServiceI jndcAccessHistoryService;

    @Override
    public void ndcServerStart() {
        Long id = getLongIdGetter().get();
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerDao.updateStatus(id, JNDCServerStatusEnum.LISTEN.value);

        JndcLogDTO jndcLogDTO = new JndcLogDTO();
        jndcLogDTO.setLogType("server");
        jndcLogDTO.setLogTime(LocalDateTime.now());
        jndcLogDTO.setSourceId(id);
        jndcLogDTO.setLogContent("JNDC服务启动");
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcLogServiceI.save(jndcLogDTO);
    }

    @Override
    public void ndcServerStartFail(Exception e) {
        Long id = getLongIdGetter().get();
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerDao.updateStatus(id, JNDCServerStatusEnum.PAUSE.value);
    }

    @Override
    public void connectActive() {

    }

    @Override
    public void openChannel(String clientId, InetSocketAddress remote) {
        Long l = getLongIdGetter().get();

        JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO = new JndcServerAcceptHistoryDTO();
        jndcServerAcceptHistoryDTO.setClientId(clientId);
        jndcServerAcceptHistoryDTO.setSourceIp(remote.getHostString());
        jndcServerAcceptHistoryDTO.setSourcePort(remote.getPort());
        jndcServerAcceptHistoryDTO.setConnectTime(LocalDateTime.now());
        jndcServerAcceptHistoryDTO.setServerIdString(l.toString());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerAcceptHistoryServiceI.save(jndcServerAcceptHistoryDTO);
    }

    @Override
    public void tcpServerStartSuccess(String ndcClientId, String serviceId) {

    }

    @Override
    public void tcpServerStartFail(String ndcClientId, String serviceId) {

    }

    @Override
    public void tcpChannelActive(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress tcpRemote) {
        JndcAccessHistoryDTO jndcAccessHistoryDTO = new JndcAccessHistoryDTO();
        jndcAccessHistoryDTO.setDestination(serviceId);
        jndcAccessHistoryDTO.setRemoteIp(tcpRemote.getHostString());
        jndcAccessHistoryDTO.setRemotePort(tcpRemote.getPort());
        jndcAccessHistoryService.save(jndcAccessHistoryDTO);
    }

    @Override
    public void tcpChannelRead(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress remote, byte[] data) {

    }

    @Override
    public void tcpChannelWrite(String ndcClientId, String serviceId, String tcpChannelId, byte[] data) {

    }

    @Override
    public void tcpChannelInactive(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress remote) {

    }

    @Override
    public void tcpServerStop(String ndcClientId, String serviceId) {

    }

    @Override
    public void connectInActive(String clientId) {
        jndcServerAcceptHistoryServiceI.updateDisconnectTime(clientId, LocalDateTime.now());
    }

    @Override
    public void ndcServerStop() {
        Long id = getLongIdGetter().get();
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerDao.updateStatus(id, JNDCServerStatusEnum.PAUSE.value);

        JndcLogDTO jndcLogDTO = new JndcLogDTO();
        jndcLogDTO.setLogType("server");
        jndcLogDTO.setLogTime(LocalDateTime.now());
        jndcLogDTO.setSourceId(id);
        jndcLogDTO.setLogContent("JNDC服务停止");
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcLogServiceI.save(jndcLogDTO);
    }
}
