package com.view.jndc.server.model.ndc.plugins;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.server.model.TraceableEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;


@Data
@Slf4j
@TableName("plugin_time_range")
public class TimeRangeEntity extends TraceableEntity {


    @TableField(value = "range_start")
    private LocalTime rangeStart;

    @TableField(value = "range_end")
    private LocalTime rangeEnd;

    public static String ddl() {
        return "create table if not exists plugin_time_range (range_id bigint primary key, range_start time, range_end time, create_date datetime, update_date datetime)";
    }
}
