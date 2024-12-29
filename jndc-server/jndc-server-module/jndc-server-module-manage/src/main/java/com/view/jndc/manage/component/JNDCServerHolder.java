package com.view.jndc.manage.component;

import com.google.common.eventbus.AsyncEventBus;
import com.view.core.server.ndc.NDCServer;
import com.view.jndc.manage.dao.jndc_server.JndcServerDao;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import com.view.jndc.manage.serviceI.jndc_access_history.JndcAccessHistoryServiceI;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import com.view.jndc.manage.serviceI.jndc_server_accept_history.JndcServerAcceptHistoryServiceI;
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

    private final AsyncEventBus asyncEventBus;

    private final JndcServerDao jndcServerDao;

    private final JndcLogServiceI jndcLogServiceI;

    private final JndcServerAcceptHistoryServiceI jndcServerAcceptHistoryServiceI;

    private final JndcAccessHistoryServiceI jndcAccessHistoryServiceI;

    public void startServer(JndcServerDTO jndcServerDTO) {
        executorService.submit(() -> {

        });
    }

    public void startServer() {


    }

    public void stopServer(JndcServerDTO jndcServerDTO) {

    }
}
