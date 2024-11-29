package com.view.jndc.server.model.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.view.jndc.server.model.TraceableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;


@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class AdminUserEntity extends TraceableEntity {

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "password")
    private String password;

    @TableField(value = "role")
    private String role;

    public static String ddl() {
        return "create table if not exists admin_user (id bigint primary key, user_name varchar(255), password varchar(255), role varchar(255), create_date datetime, update_date datetime)";
    }
}
