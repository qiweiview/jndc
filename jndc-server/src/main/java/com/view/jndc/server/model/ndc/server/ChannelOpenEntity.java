package com.view.jndc.server.model.ndc.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.server.model.TraceableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 通道打开
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@TableName("channel_open")
public class ChannelOpenEntity extends TraceableEntity implements Serializable {

    @TableField(value = "ndc_client_id")
    private String ndcClientId;

    @TableField(value = "ndc_server_id")
    private String ndcServerId;

    public static String ddl() {
        return "create table if not exists channel_open (id bigint primary key, ndc_client_id varchar(255), ndc_server_id varchar(255), create_date datetime, update_date datetime)";
    }

}
