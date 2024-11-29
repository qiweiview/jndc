package com.view.jndc.server.model.ndc.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.server.model.TraceableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@TableName("ndc_server")
public class NDCServerEntity extends TraceableEntity {

    @TableField(value = "crate_time")
    private String serverName;

    private Integer listenPort;

    private String server_state;

    public static String ddl() {
        return "create table if not exists ndc_server (id bigint primary key, server_name varchar(255), listen_port int, server_state varchar(255), create_date datetime, update_date datetime)";
    }
}
