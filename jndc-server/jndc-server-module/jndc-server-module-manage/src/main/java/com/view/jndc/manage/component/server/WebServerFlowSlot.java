package com.view.jndc.manage.component.server;

import com.view.core.server.ndc.flow.ServerFlowSlot;
import com.view.core.server.tcp.TCPServerConfiguration;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.jndc.manage.dao.jndc_server.JndcServerDao;
import com.view.jndc.manage.enums.server.JNDCServerAPPStatus;
import com.view.jndc.manage.enums.server.JNDCServerStatusEnum;
import com.view.jndc.manage.model.jndc_access_history.dto.JndcAccessHistoryDTO;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.model.jndc_server_accept_history.dto.JndcServerAcceptHistoryDTO;
import com.view.jndc.manage.model.jndc_server_app.dto.JndcServerAppDTO;
import com.view.jndc.manage.serviceI.jndc_access_history.JndcAccessHistoryServiceI;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import com.view.jndc.manage.serviceI.jndc_server_accept_history.JndcServerAcceptHistoryServiceI;
import com.view.jndc.manage.serviceI.jndc_server_app.JndcServerAppServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebServerFlowSlot extends ServerFlowSlot {
    private final JndcServerDao jndcServerDao;

    private final JndcServerAcceptHistoryServiceI jndcServerAcceptHistoryServiceI;

    private final JndcLogServiceI jndcLogServiceI;

    private final JndcAccessHistoryServiceI jndcAccessHistoryService;

    private final JndcServerAppServiceI jndcServerAppServiceI;

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
    public void tcpServerStartSuccess(TCPServerConfiguration   tcpServerConfiguration) {
        String serviceId = tcpServerConfiguration.getServiceId();
        String ndcClientId = tcpServerConfiguration.getNdcClientId();
        Long serverId = getLongIdGetter().get();

        JndcServerAppDTO jndcServerAppDTO = jndcServerAppServiceI.getByServiceId(serviceId);
        if (jndcServerAppDTO == null) {
            jndcServerAppDTO = new JndcServerAppDTO();
            jndcServerAppDTO.setServerId(serverId);
            jndcServerAppDTO.setSourceServiceId(serviceId);
            jndcServerAppDTO.setSourceClientId(ndcClientId);
            jndcServerAppDTO.setBindHost("0.0.0.0");
            jndcServerAppDTO.setBindPort(tcpServerConfiguration.getPort());
            jndcServerAppDTO.setBindStatus(JNDCServerAPPStatus.LISTEN.value);
            jndcServerAppServiceI.save(jndcServerAppDTO);
        }else {
            jndcServerAppDTO.setBindStatus(JNDCServerAPPStatus.LISTEN.value);
            jndcServerAppServiceI.updateById(jndcServerAppDTO);
        }

    }

    @Override
    public void tcpServerStartFail(String ndcClientId, String serviceId) {
        jndcServerAppServiceI.updateStatusByServiceId(serviceId, JNDCServerAPPStatus.PAUSE.value);
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
    public void serviceRegister(String serverId, String ndcClientId, String serviceId) {
        log.info("服务注册:{} {} {}", serverId, ndcClientId, serviceId);
    }

    @Override
    public void serviceUnRegister(String serverId, String ndcClientId, String serviceId) {
        log.info("服务注销:{} {} {}", serverId, ndcClientId, serviceId);
    }


    @Override
    public void tcpChannelWrite(String ndcClientId, String serviceId, String tcpChannelId, byte[] data) {

    }

    @Override
    public void tcpChannelInactive(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress remote) {

    }

    @Override
    public void tcpServerStop(String ndcClientId, String serviceId) {
        jndcServerAppServiceI.updateStatusByServiceId(serviceId, JNDCServerAPPStatus.PAUSE.value);
    }

    @Override
    public void connectInActive(String clientId) {
        jndcServerAcceptHistoryServiceI.updateDisconnectTime(clientId, LocalDateTime.now());
    }

    @Override
    protected void clientHeartBeat(String ndcClientId, long timestamp) {
        //System.currentTimeMillis()转LocalDateTime

        // 将时间戳转换为Instant对象
        Instant instant = Instant.ofEpochMilli(timestamp);

        // 根据Instant对象和系统默认时区获取ZonedDateTime对象
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());

        // 从ZonedDateTime对象中提取LocalDateTime对象
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

        jndcServerAcceptHistoryServiceI.updateLatestHeartBeatTime(ndcClientId, localDateTime);

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
