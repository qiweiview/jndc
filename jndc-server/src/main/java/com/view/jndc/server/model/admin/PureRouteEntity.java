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
@TableName("pure_route")
public class PureRouteEntity extends TraceableEntity {
    @TableField(value = "path")
    private String path;

    @TableField(value = "name")
    private String name;

    @TableField(value = "component")
    private String component;

    @TableField(value = "pure_meta")
    private PureMetaEntity meta;

    @TableField(exist = false)
    private List<PureRouteEntity> children;  // Child routes


}

