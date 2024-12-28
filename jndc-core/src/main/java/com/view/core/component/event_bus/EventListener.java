package com.view.core.component.event_bus;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.view.core.component.SupportEnvironment;
import com.view.core.model.TCPDataTransport;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class EventListener {
    private SupportEnvironment supportEnvironment;





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
