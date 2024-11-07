package com.view.core.component;

import com.google.common.eventbus.AsyncEventBus;
import com.view.core.client.ndc.NDCClient;
import com.view.core.component.app_center.AppCenter;
import com.view.core.component.event_bus.EventListener;
import com.view.core.server.ndc.NDCServer;

import java.util.concurrent.Executors;

public class GlobalBeanContext {
    public static final EventListener EVENT_LISTENER = new EventListener();
    public static final AsyncEventBus EVENT_BUS = new AsyncEventBus(Executors.newCachedThreadPool());
    public static NDCServer NDC_SERVER;
    public static NDCClient NDC_CLIENT;
    public static final AppCenter APP_CENTER = new AppCenter();

    static {
        EVENT_BUS.register(EVENT_LISTENER);
    }
}
