package com.view.jndc.server.model.ndc.plugins;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.server.model.TraceableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;


@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@TableName("ip_record")
public class IPRecordEntity extends TraceableEntity {

    @TableField(value = "ip")
    private String ip;

    @TableField(value = "last_active_time")
    private Long lastActiveTime;

    @TableField(value = "total_traffic")
    private Long totalTraffic;


    public static String ddl() {
        return "create table if not exists ip_record (id bigint primary key, ip varchar(255), last_active_time datetime, total_traffic bigint, create_date datetime, update_date datetime)";
    }
}
