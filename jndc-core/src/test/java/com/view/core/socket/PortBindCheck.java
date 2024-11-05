package com.view.core.socket;

import com.view.core.component.app_center.AppCenter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class PortBindCheck {


    @Test
    public void run() {
        boolean b = AppCenter.portBindable(3306);
        log.info("端口是否可绑定：{}", b);
    }
}
