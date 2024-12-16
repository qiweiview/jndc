package com.view.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-01 22:20
 * @description: 角色修改对象
 */
@Data
public class SysRoleUpdateDTO {

    /**
     * ID
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 状态（0正常 1停用）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    private String idString;

    public void setId( Long id) {
        this.id = id;
        if (id != null&&idString==null) {
            this.idString = id.toString();
        }
    }

    public void setIdString(String idString) {
        this.idString = idString;
        if (idString != null) {
            this.id = Long.parseLong(idString);
        }
    }
}
