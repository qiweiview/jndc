package com.view.dto.role;

import com.view.model.dto.BasePageDTO;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-01 21:04
 * @description: 角色查询对象
 */
@Data
public class RoleQueryDTO extends BasePageDTO {

    private String roleName;

    private String roleCode;

    private Integer status;
}
