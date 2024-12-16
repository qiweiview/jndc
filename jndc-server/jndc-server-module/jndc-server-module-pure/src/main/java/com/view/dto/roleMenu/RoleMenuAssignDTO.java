package com.view.dto.roleMenu;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-05 20:25
 * @description: 菜单分配
 */
@Data
public class RoleMenuAssignDTO {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    private Long userId;

    private Set<Long> menuIds;

    private String userIdString;

    public void setUserId(Long userId) {
        this.userId = userId;
        if (userId != null && userIdString == null) {
            this.userIdString = userId.toString();
        }
    }

    public void setUserIdString(String userIdString) {
        this.userIdString = userIdString;
        if (userIdString != null) {
            this.userId = Long.parseLong(userIdString);
        }
    }
}
