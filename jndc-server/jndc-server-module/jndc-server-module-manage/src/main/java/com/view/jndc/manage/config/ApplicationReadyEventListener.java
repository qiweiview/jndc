package com.view.jndc.manage.config;


import com.view.jndc.manage.serviceI.jndc_client.JndcClientServiceI;
import com.view.jndc.manage.serviceI.jndc_server.JndcServerServiceI;
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


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof WebServerInitializedEvent) {
            jndcClientServiceI.resetAllClientStatus();
            jndcServerServiceI.resetAllServerStatus();
        }
    }
}
