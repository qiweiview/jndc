package com.view.core.component;

import com.google.common.eventbus.AsyncEventBus;
import com.view.core.client.ndc.NDCClient;
import com.view.core.component.app_center.AppCenter;
import com.view.core.component.app_center.ServiceIdManager;
import com.view.core.component.event_bus.EventListener;
import com.view.core.server.ndc.NDCServer;

import java.util.concurrent.Executors;

/**
 * 配套环境
 */
public class SupportEnvironment {

    public  ServiceIdManager SERVICE_ID_MANAGER = new ServiceIdManager();

    public  EventListener EVENT_LISTENER = new EventListener();

    public  AsyncEventBus EVENT_BUS = new AsyncEventBus(Executors.newCachedThreadPool());

    public  AppCenter APP_CENTER = new AppCenter();

    public NDCServer NDC_SERVER;

    public NDCClient NDC_CLIENT;

    public SupportEnvironment() {
        EVENT_BUS.register(EVENT_LISTENER);
        EVENT_LISTENER.setSupportEnvironment(this);
        APP_CENTER.setSupportEnvironment(this);
    }
}
