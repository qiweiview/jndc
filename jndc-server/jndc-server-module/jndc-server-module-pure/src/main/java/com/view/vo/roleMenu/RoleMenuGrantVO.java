package com.view.vo.roleMenu;

import lombok.Data;

import java.util.List;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-02 20:42
 * @description: 菜单授予显示对象
 */
@Data
public class RoleMenuGrantVO {

    private Long id;

    private Long parentId;

    private String title;

    private Integer type;

    private String typeStr;

    private Integer sortOrder;

    private Long roleMenuId;


    List<RoleMenuGrantVO> children;
}
