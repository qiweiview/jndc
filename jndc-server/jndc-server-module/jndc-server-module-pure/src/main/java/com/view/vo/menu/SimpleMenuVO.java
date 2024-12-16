package com.view.vo.menu;

import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-03 8:58
 * @description: 简单菜单对象
 */
@Data
public class SimpleMenuVO {

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer sortOrder;


    /**
     * 菜单类型（0菜单 1iframe 2外链 3按钮）
     */
    private Integer type;

    private String idString;

    public void setId(Long id) {
        this.id = id;
        if (id != null&&idString==null) {
            this.idString = id.toString();
        }
    }

    public void setIdString(String idString) {
        this.idString = idString;
        if (idString != null&&id==null) {
            this.id = Long.parseLong(idString);
        }
    }

    private String parentIdString;

    public void setParentId(Long parentId) {
        this.parentId = parentId;
        if (parentId != null&&parentIdString==null) {
            this.parentIdString = parentId.toString();
        }
    }

    public void setParentIdString(String parentIdString) {
        this.parentIdString = parentIdString;
        if (parentIdString != null) {
            this.parentId = Long.parseLong(parentIdString);
        }
    }
}
