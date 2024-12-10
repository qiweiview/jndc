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


}
