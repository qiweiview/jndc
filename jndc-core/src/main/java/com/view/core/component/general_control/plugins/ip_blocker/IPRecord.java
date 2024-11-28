package com.view.core.component.general_control.plugins.ip_blocker;

import com.view.core.model.TraceableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class IPRecord extends TraceableEntity {
    private String ip;

    private long lastActiveTime;

    private long totalTraffic;

    public void totalTrafficIncrease() {
        totalTraffic++;
    }


    public static String ddl() {
        return "create table if not exists ip_record (id bigint primary key, ip varchar(255), last_active_time datetime, total_traffic bigint, create_date datetime, update_date datetime)";
    }
}
