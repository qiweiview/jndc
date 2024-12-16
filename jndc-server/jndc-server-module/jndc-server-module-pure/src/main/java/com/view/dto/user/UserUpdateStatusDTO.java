package com.view.dto.user;

import com.view.enums.BusinessStatusEnum;
import com.view.validator.EnumValue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-04 15:58
 * @description: 用户状态更改
 */
@Data
public class UserUpdateStatusDTO {
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 状态（0正常，1禁用）
     */
    @NotNull(message = "状态不能为空")
    @EnumValue(enumClass = BusinessStatusEnum.class, message = "状态只能为0（正常）或1（冻结）")
    private Integer status;

    private String idString;

}
