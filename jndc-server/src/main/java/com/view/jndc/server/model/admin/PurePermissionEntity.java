package com.view.jndc.server.model.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.server.model.TraceableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@TableName("pure_permission")
public class PurePermissionEntity extends TraceableEntity {
    @TableField(value = "title")
    private String path;

    @TableField(exist = false)
    private PureMetaEntity meta;

    @TableField(exist = false)
    private List<PureRouteEntity> children;  // List of child routes

    public static String ddl() {
        return "create table if not exists pure_permission (\n" +
                "    id bigint auto_increment primary key,\n" +
                "    path varchar(255),\n" +
                "    create_time datetime,\n" +
                "    update_time datetime\n" +
                ");";
    }
}
