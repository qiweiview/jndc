package com.view.core.component.general_control.plugins.time_blocker;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;


@Data
@Slf4j
public class TimeRange {
    private Long rangeId;

    private LocalTime rangeStart;

    private LocalTime rangeEnd;


}
