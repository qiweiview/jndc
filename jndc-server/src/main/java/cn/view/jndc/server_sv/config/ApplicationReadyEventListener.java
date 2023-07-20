package cn.view.jndc.server_sv.config;


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

    public static Integer RUNNING_PORT;


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        String logo = "\n" +
                "       _   _   _ _____   _____             _____ ______ _______      ________ _____  \n" +
                "      | | | \\ | |  __ \\ / ____|           / ____|  ____|  __ \\ \\    / /  ____|  __ \\ \n" +
                "      | | |  \\| | |  | | |       ______  | (___ | |__  | |__) \\ \\  / /| |__  | |__) |\n" +
                "  _   | | | . ` | |  | | |      |______|  \\___ \\|  __| |  _  / \\ \\/ / |  __| |  _  / \n" +
                " | |__| | | |\\  | |__| | |____            ____) | |____| | \\ \\  \\  /  | |____| | \\ \\ \n" +
                "  \\____/  |_| \\_|_____/ \\_____|          |_____/|______|_|  \\_\\  \\/   |______|_|  \\_\\\n" +
                "                                                                                     \n" +
                "                                                                                     ";


        WebServerInitializedEvent webServerInitializedEvent = (WebServerInitializedEvent) event;
        RUNNING_PORT = webServerInitializedEvent.getWebServer().getPort();
        log.info(logo + "\n" + "---------------启动成功--------------- document address http://127.0.0.1:" + RUNNING_PORT + "/swagger-ui.html");


    }
}