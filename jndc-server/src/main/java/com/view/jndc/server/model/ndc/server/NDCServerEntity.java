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

    @TableField(value = "serve_name")
    private String serverName;

    @TableField(value = "server_unique_id")
    private String serverUniqueId;

    @TableField(value = "listen_port")
    private Integer listenPort;

    @TableField(value = "server_state")
    private String server_state;

    public static String ddl() {
        return "create table if not exists ndc_server (id bigint primary key, server_name varchar(255), listen_port int, server_state varchar(255), create_date datetime, update_date datetime)";
    }
}
