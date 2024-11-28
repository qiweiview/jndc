package com.view.core.component.general_control.plugins.time_blocker;

import lombok.Data;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 时间阻断器
 */
@Data
public class TimeBlocker {

    //阻断IP集合
    private Set<TimeRange> timeRanges = new HashSet<>();

    public boolean checkBlock(LocalTime time) {
        for (TimeRange timeRange : timeRanges) {
            if (time.isAfter(timeRange.getRangeStart()) && time.isBefore(timeRange.getRangeEnd())) {
                return true;
            }
        }
        return false;
    }

    public void addTimeRange(LocalTime start, LocalTime end) {
        TimeRange timeRange = new TimeRange();
        timeRange.setRangeStart(start);
        timeRange.setRangeEnd(end);
        timeRanges.add(timeRange);
    }



}
