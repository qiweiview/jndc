package com.view.dto.menu;

import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-02 22:34
 * @description: 菜单查询
 */
@Data
public class MenuQueryDTO {

    private Long id;

    private Integer visible;

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
