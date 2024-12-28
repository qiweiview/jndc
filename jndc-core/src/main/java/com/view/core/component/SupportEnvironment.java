package com.view.core.component;

import com.google.common.eventbus.AsyncEventBus;
import com.view.core.client.ndc.NDCClient;
import com.view.core.component.app_center.ServiceIdManager;
import com.view.core.component.event_bus.EventListener;
import com.view.core.server.ndc.NDCServer;

import java.util.concurrent.Executors;

/**
 * 配套环境
 */
public class SupportEnvironment {

    public  EventListener EVENT_LISTENER = new EventListener();

    public  AsyncEventBus EVENT_BUS = new AsyncEventBus(Executors.newCachedThreadPool());

    public SupportEnvironment() {
        EVENT_BUS.register(EVENT_LISTENER);
        EVENT_LISTENER.setSupportEnvironment(this);
    }
}
