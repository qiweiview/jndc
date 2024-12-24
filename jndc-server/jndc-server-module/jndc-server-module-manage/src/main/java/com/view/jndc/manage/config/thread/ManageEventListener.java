package com.view.jndc.manage.config.thread;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManageEventListener {




    @AllowConcurrentEvents
    @Subscribe
    public void acceptChannelOperation(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.warn("异步事件执行失败", e);
        }
    }


}