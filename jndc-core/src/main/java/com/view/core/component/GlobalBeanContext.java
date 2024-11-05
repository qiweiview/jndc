package com.view.core.component;

import com.view.core.component.app_center.AppCenter;
import com.view.core.component.data_hub.NativePackageHub;

public class GlobalBeanContext {
    public static final NativePackageHub PACKAGE_HUB = new NativePackageHub();
    public static final AppCenter APP_CENTER = new AppCenter();
}
