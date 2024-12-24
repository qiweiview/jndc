package com.view.jndc.manage.config.thread;

import com.google.common.eventbus.AsyncEventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class BeanConfiguration {


    @Bean
    public AsyncEventBus getAsyncEventBus(ManageEventListener manageEventListener){
        AsyncEventBus asyncEventBus = new AsyncEventBus(Executors.newVirtualThreadPerTaskExecutor());
        asyncEventBus.register(manageEventListener);
        return asyncEventBus;
    }

    @Bean
    public ManageEventListener getManageEventListener(){
        return new ManageEventListener();
    }
}
