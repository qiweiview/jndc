package com.view.jndc.server;

import com.view.jndc.server.config.dynamic_datasource.DynamicDataSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;


@SpringBootApplication(scanBasePackages = "com.view.jndc.server", exclude = {DataSourceAutoConfiguration.class})
@Import({DynamicDataSourceConfig.class})
public class JNDCServer {

    public static void main(String[] args) {
        SpringApplication.run(JNDCServer.class, args);
    }

}
