package com.view.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-06-05 14:35
 * @description: 菜单修改
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuUpdateDTO {
    /**
     * 菜单ID
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    private String title;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 父菜单ID
     */
    @NotNull(message = "父菜单id不能为空")
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
     * 是否缓存（0缓存 1不缓存）
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
     * 备注
     */
    private String remark;

    /**
     * 菜单所属平台（0后台 1前台）
     */
    private Integer platformType;

    /**
     * 进场动画
     */
    private String enterTransition;

    /**
     * 出场动画
     */
    private String leaveTransition;
}
