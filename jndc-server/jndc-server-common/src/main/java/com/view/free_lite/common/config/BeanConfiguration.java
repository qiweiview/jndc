package com.view.free_lite.common.config;



import com.view.free_lite.common.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Value("${snowflake.datacenter-id}")
    private Long datacenterId;

    @Value("${snowflake.worker-id}")
    private Long workerId;

    @Bean
    public SnowflakeIdWorker getSnowflakeIdGenerator() {
        return new SnowflakeIdWorker(datacenterId, workerId);
    }
}
