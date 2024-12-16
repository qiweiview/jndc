package com.view.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 菜单权限表(SysMenu)表实体类
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysMenu {
    /**
     * 菜单ID
     */
    @TableId
    private Long id;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer sortOrder;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 是否缓存（0不缓存 1缓存）
     */
    private Integer cacheFlag;

    /**
     * 菜单类型（0菜单 1iframe 2外链 3按钮）
     */
    private Integer type;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    private Integer visible;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单所属平台（0后台 1前台）
     */
    private Integer platformType;


    /**
     * 路由重定向
     */
    private String redirect;

    /**
     * 内嵌的iframe页面是否开启首次加载动画（0否 1是）
     */
    private Integer frameLoading;

    /**
     * iframe页面地址
     */
    private String frameSrc;

    @TableField(exist = false)
    private String parentIdString;

    public void setParentIdString(String parentIdString) {
        this.parentIdString = parentIdString;
        if (parentIdString != null) {
            this.parentId = Long.parseLong(parentIdString);
        }
    }

    public  void setParentId(Long parentId) {
        this.parentId = parentId;
        if (parentId != null && parentIdString == null) {
            this.parentIdString = parentId.toString();
        }
    }

    @TableField(exist = false)
    private String idString;

    public void setIdString(String idString) {
        this.idString = idString;
        if (idString != null) {
            this.id = Long.parseLong(idString);
        }
    }

    public void setId(Long id) {
        this.id = id;
        if (id != null && idString == null) {
            this.idString = id.toString();
        }
    }
}

