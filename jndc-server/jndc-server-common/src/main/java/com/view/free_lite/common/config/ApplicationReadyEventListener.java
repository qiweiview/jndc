package com.view.free_lite.common.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationEvent> {



    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        String logo = "=================fll-server=================";
        WebServerInitializedEvent webServerInitializedEvent = (WebServerInitializedEvent) event;

        int port = webServerInitializedEvent.getWebServer().getPort();


        log.info(logo + "\n" + "---------------启动成功--------------- document address http://127.0.0.1:" + port + "/");

    }
}
