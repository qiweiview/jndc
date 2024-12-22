package com.view.jndc.manage.component;

import com.view.core.server.ndc.NDCServer;
import com.view.core.server.ndc.NDCServerConfiguration;
import com.view.jndc.manage.dao.jndc_server.JndcServerDao;
import com.view.jndc.manage.enums.JNDCServerStatusEnum;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import com.view.jndc.manage.model.jndc_server_accept_history.d_o.JndcServerAcceptHistoryDO;
import com.view.jndc.manage.model.jndc_server_accept_history.dto.JndcServerAcceptHistoryDTO;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import com.view.jndc.manage.serviceI.jndc_server_accept_history.JndcServerAcceptHistoryServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Component
@Slf4j
public class JNDCServerHolder {
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private Map<String, NDCServer> serverMap = new ConcurrentHashMap<>();

    private Set<Integer> bindPortSet = new ConcurrentSkipListSet<>();

    private final JndcServerDao jndcServerDao;

    private final JndcLogServiceI jndcLogServiceI;

    private final JndcServerAcceptHistoryServiceI jndcServerAcceptHistoryServiceI;

    public void startServer(JndcServerDTO jndcServerDTO) {
        executorService.submit(() -> {

            Long id = jndcServerDTO.getId();
            String uniqueId = jndcServerDTO.getUniqueId();
            Integer bindPort = jndcServerDTO.getBindPort();

            if (bindPortSet.contains(bindPort)) {
                log.warn("端口已被占用");
                jndcServerDao.updateStatus(id, JNDCServerStatusEnum.PAUSE.value);
                return;
            }

            NDCServer ndcServer = new NDCServer();
            try {
                bindPortSet.add(bindPort);
                serverMap.put(uniqueId, ndcServer);
                jndcServerDao.updateStatus(id, JNDCServerStatusEnum.PROCESSING.value);
            } catch (Exception e) {
                log.error("参数更新失败", e);
            }

            try {
                NDCServerConfiguration jndcServerConfiguration = new NDCServerConfiguration();
                jndcServerConfiguration.setHost(jndcServerDTO.getBindHost());
                jndcServerConfiguration.setPort(bindPort);
                jndcServerConfiguration.setUniqueId(uniqueId);
                jndcServerConfiguration.setStartedCallback(() -> {
                    //todo 启动回调
                    jndcServerDao.updateStatus(id, JNDCServerStatusEnum.LISTEN.value);

                    JndcLogDTO jndcLogDTO = new JndcLogDTO();
                    jndcLogDTO.setLogType("server");
                    jndcLogDTO.setLogTime(LocalDateTime.now());
                    jndcLogDTO.setSourceId(id);
                    jndcLogDTO.setLogContent("服务启动");
                    jndcLogServiceI.save(jndcLogDTO);
                });

                jndcServerConfiguration.setStopCallback(() -> {
                    //todo 停止回调
                    jndcServerDao.updateStatus(id, JNDCServerStatusEnum.PAUSE.value);
                    bindPortSet.remove(bindPort);
                    serverMap.remove(uniqueId);

                    JndcLogDTO jndcLogDTO = new JndcLogDTO();
                    jndcLogDTO.setLogType("server");
                    jndcLogDTO.setLogTime(LocalDateTime.now());
                    jndcLogDTO.setSourceId(id);
                    jndcLogDTO.setLogContent("服务停止");
                    jndcLogServiceI.save(jndcLogDTO);

                });

                jndcServerConfiguration.setFailCallback((e) -> {
                    //todo 启动异常回调
                    jndcServerDao.updateStatus(id, JNDCServerStatusEnum.PAUSE.value);

                    JndcLogDTO jndcLogDTO = new JndcLogDTO();
                    jndcLogDTO.setLogType("server");
                    jndcLogDTO.setLogTime(LocalDateTime.now());
                    jndcLogDTO.setSourceId(id);
                    jndcLogDTO.setLogContent(e.getMessage());
                    jndcLogServiceI.save(jndcLogDTO);

                    bindPortSet.remove(bindPort);
                    serverMap.remove(uniqueId);
                });


                /*------------------- 会话部分 -------------------*/
                jndcServerConfiguration.setConnectActive((e) -> {
                    //todo 连接激活


                    JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO = new JndcServerAcceptHistoryDTO();
                    jndcServerAcceptHistoryDTO.setServerId(id);
                    jndcServerAcceptHistoryDTO.setConnectTime(LocalDateTime.now());
                    jndcServerAcceptHistoryDTO.setSourceIp(e.getHost());
                    jndcServerAcceptHistoryDTO.setSourcePort(e.getPort());
                    JndcServerAcceptHistoryDO save = jndcServerAcceptHistoryServiceI.save(jndcServerAcceptHistoryDTO);

                    e.setAcceptHistoryId(save.getId());
                    return e;
                });

                jndcServerConfiguration.setConnectInActive((e) -> {
                    //todo 连接失活
                    Long acceptHistoryId = e.getAcceptHistoryId();
                    JndcServerAcceptHistoryDTO byId = jndcServerAcceptHistoryServiceI.getById(acceptHistoryId);
                    if (byId == null) {
                        log.warn("连接历史记录不存在");
                    } else {
                        byId.setInterruptTime(LocalDateTime.now());
                        jndcServerAcceptHistoryServiceI.updateById(byId);
                    }


                });


                ndcServer.start(jndcServerConfiguration);
            } catch (Exception e) {
                bindPortSet.remove(bindPort);
                serverMap.remove(uniqueId);
            }
        });
    }

    public void stopServer(JndcServerDTO jndcServerDTO) {
        String uniqueId = jndcServerDTO.getUniqueId();
        NDCServer ndcServer = serverMap.get(uniqueId);
        if (ndcServer == null) {
            log.warn("服务:{}未启动", uniqueId);
        } else {
            ndcServer.stop();
            serverMap.remove(uniqueId);
            Long id = jndcServerDTO.getId();
            jndcServerDao.updateStatus(id, JNDCServerStatusEnum.PAUSE.value);
        }
    }
}
