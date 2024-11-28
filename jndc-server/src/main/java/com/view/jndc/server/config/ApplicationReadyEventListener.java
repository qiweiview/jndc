package com.view.jndc.server.config;


import com.view.jndc.server.config.dynamic_datasource.TableInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationReadyEventListener {

    private final TableInitializer tableInitializer;

    @EventListener
    public void onApplicationEvent(ApplicationEvent event) {
        String logo = "=================jndc-server=================";
        WebServerInitializedEvent webServerInitializedEvent = (WebServerInitializedEvent) event;

        int port = webServerInitializedEvent.getWebServer().getPort();

        tableInitializer.init();


        log.info(logo + "\n" + "---------------启动成功--------------- document address http://127.0.0.1:" + port + "/swagger-ui.html" +
                "\n" + "---------------Druid管理页--------------- document address http://127.0.0.1:" + port + "/druid/login.html");


    }

}