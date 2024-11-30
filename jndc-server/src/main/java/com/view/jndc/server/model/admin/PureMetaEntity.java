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
@TableName("pure_meta")
public class PureMetaEntity extends TraceableEntity {
    @TableField(value = "title")
    private String title;

    @TableField(value = "icon")
    private String icon;

    @TableField(value = "rank")
    private Integer rank;

    @TableField(value = "roles")
    private List<String> roles;  // List of roles (e.g., "admin", "common")

    @TableField(value = "auths")
    private List<String> auths;  // List of permissions (e.g., "permission:btn:add")

    public static String ddl() {
        return "create table if not exists pure_meta (\n" +
                "    id bigint auto_increment primary key,\n" +
                "    title varchar(255),\n" +
                "    icon varchar(255),\n" +
                "    rank int,\n" +
                "    roles varchar(255),\n" +
                "    auths varchar(255),\n" +
                "    create_time datetime,\n" +
                "    update_time datetime\n" +
                ");";
    }
}
