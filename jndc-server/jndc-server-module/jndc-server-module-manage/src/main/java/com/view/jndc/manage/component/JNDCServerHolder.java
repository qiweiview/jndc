package com.view.jndc.manage.component;

import com.view.core.server.ndc.NDCServer;
import com.view.jndc.manage.dao.jndc_server.JndcServerDao;
import com.view.jndc.manage.enums.JNDCServerStatusEnum;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
            bindPortSet.add(bindPort);
            jndcServerDao.updateStatus(id, JNDCServerStatusEnum.PROCESSING.value);
            try {
                ndcServer.start(bindPort, () -> {
                    //todo 启动回调
                    jndcServerDao.updateStatus(id, JNDCServerStatusEnum.LISTEN.value);
                }, (e) -> {
                    //todo 启动异常回调
                    jndcServerDao.updateStatus(id, JNDCServerStatusEnum.PAUSE.value);

                    JndcLogDTO jndcLogDTO = new JndcLogDTO();
                    jndcLogDTO.setLogType("server");
                    jndcLogDTO.setSourceId(id);
                    jndcLogDTO.setLogContent(e.getMessage());
                    jndcLogServiceI.save(jndcLogDTO);

                }, uniqueId);
            } catch (Exception e) {
                bindPortSet.remove(bindPort);
            }
        });
    }

    public void stopServer(JndcServerDTO jndcServerDTO) {
        String uniqueId = jndcServerDTO.getUniqueId();
        NDCServer ndcServer = serverMap.get(uniqueId);
        if (ndcServer != null) {
            ndcServer.stop();
            serverMap.remove(uniqueId);
            Long id = jndcServerDTO.getId();
            jndcServerDao.updateStatus(id, JNDCServerStatusEnum.PAUSE.value);
        }
    }
}
