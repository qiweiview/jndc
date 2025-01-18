package com.view.jndc.manage.config;


import com.view.jndc.manage.serviceI.jndc_client.JndcClientServiceI;
import com.view.jndc.manage.serviceI.jndc_client_service.JndcClientServiceServiceI;
import com.view.jndc.manage.serviceI.jndc_server.JndcServerServiceI;
import com.view.jndc.manage.serviceI.jndc_server_accept_history.JndcServerAcceptHistoryServiceI;
import com.view.jndc.manage.serviceI.jndc_server_app.JndcServerAppServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("jndc-server-module-manage")
@Slf4j
@RequiredArgsConstructor
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationEvent> {

    private final JndcClientServiceI jndcClientServiceI;

    private final JndcServerServiceI jndcServerServiceI;

    private final JndcClientServiceServiceI jndcClientServiceServiceI;

    private final JndcServerAcceptHistoryServiceI jndcServerAcceptHistoryServiceI;

    private final JndcServerAppServiceI jndcServerAppServiceI;


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof WebServerInitializedEvent) {
            jndcClientServiceI.resetAllClientStatus();

            jndcServerServiceI.resetAllServerStatus();

            jndcClientServiceServiceI.resetAllClientServiceStatus();

            jndcServerAcceptHistoryServiceI.resetAllAcceptHistory();

            jndcServerAppServiceI.resetAllServer();
        }
    }
}
