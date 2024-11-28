package com.view.core.component.general_control.plugins.time_blocker;

import com.view.core.model.TraceableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class TimeRange extends TraceableEntity {
    private Long rangeId;

    private LocalTime rangeStart;

    private LocalTime rangeEnd;

    public static String ddl() {
        return "create table if not exists time_range (range_id bigint primary key, range_start datetime, range_end datetime, create_date datetime, update_date datetime)";
    }
}
