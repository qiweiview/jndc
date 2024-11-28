package com.view.jndc.server.config;

import com.view.jndc.server.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BeanConfiguration {


    @Bean

    public SnowflakeIdWorker getSnowflakeIdWorker() {
        return new SnowflakeIdWorker(1, 1);
    }
}
