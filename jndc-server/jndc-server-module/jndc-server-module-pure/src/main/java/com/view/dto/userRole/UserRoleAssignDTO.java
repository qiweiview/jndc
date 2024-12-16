package com.view.dto.userRole;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-01 23:44
 * @description: 角色分配
 */
@Data
public class UserRoleAssignDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "至少分配一个角色")
    private Set<Long> roleIds;
}
