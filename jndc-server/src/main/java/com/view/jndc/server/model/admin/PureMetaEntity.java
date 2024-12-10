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

    @TableField(value = "rank_number")
    private Integer rankNumber;

    @TableField(value = "roles")
    private List<String> roles;  // List of roles (e.g., "admin", "common")

    @TableField(value = "auths")
    private List<String> auths;  // List of permissions (e.g., "permission:btn:add")


}
