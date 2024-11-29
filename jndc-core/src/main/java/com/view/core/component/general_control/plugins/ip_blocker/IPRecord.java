package com.view.core.component.general_control.plugins.ip_blocker;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
public class IPRecord {
    private String ip;

    private long lastActiveTime;

    private long totalTraffic;

    public void totalTrafficIncrease() {
        totalTraffic++;
    }


}
