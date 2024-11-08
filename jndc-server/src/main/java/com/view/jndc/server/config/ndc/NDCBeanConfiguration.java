package com.view.jndc.server.config.ndc;

import com.view.core.server.ndc.NDCServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class NDCBeanConfiguration {

    @Value("${ndc.server.port}")
    private Integer port;

    @Bean
    public NDCServer getNDCServer() {
        NDCServer ndcServer = new NDCServer();
        new Thread(() -> {
            ndcServer.start(port, () -> {
                //todo 启动成功回调
            });
        }).start();
        return ndcServer;
    }
}
