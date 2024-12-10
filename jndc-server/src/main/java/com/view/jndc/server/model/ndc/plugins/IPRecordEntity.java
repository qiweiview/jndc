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
@TableName("plugin_ip_record")
public class IPRecordEntity extends TraceableEntity {

    @TableField(value = "ip")
    private String ip;

    @TableField(value = "last_active_time")
    private Long lastActiveTime;

    @TableField(value = "total_traffic")
    private Long totalTraffic;



}
