package com.view.core.component;

import com.google.common.eventbus.AsyncEventBus;
import com.view.core.client.ndc.NDCClient;
import com.view.core.component.app_center.AppCenter;
import com.view.core.component.app_center.ServiceIdManager;
import com.view.core.component.event_bus.EventListener;
import com.view.core.component.general_control.GeneralControl;
import com.view.core.component.general_control.plugins.IPBlocker;
import com.view.core.component.general_control.plugins.TimeBlocker;
import com.view.core.server.ndc.NDCServer;

import java.util.concurrent.Executors;

/**
 * 全局Bean上下文
 */
public class GlobalBeanContext {
    public static final TimeBlocker TIME_BLOCKER = new TimeBlocker();
    public static final IPBlocker IP_BLOCKER = new IPBlocker();
    public static final GeneralControl GENERAL_CONTROL = new GeneralControl();
    public static final ServiceIdManager SERVICE_ID_MANAGER = new ServiceIdManager();
    public static final EventListener EVENT_LISTENER = new EventListener();
    public static final AsyncEventBus EVENT_BUS = new AsyncEventBus(Executors.newCachedThreadPool());
    public static final AppCenter APP_CENTER = new AppCenter();
    public static NDCServer NDC_SERVER;
    public static NDCClient NDC_CLIENT;

    static {
        EVENT_BUS.register(EVENT_LISTENER);
    }
}
