package com.view.dto.dict;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-06-26 22:15
 * @description: 字典修改
 */
@Data
public class SysDictUpdateDTO {

    @NotNull(message = "id不能为空")
    private Long id;


    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    private String dictName;

    /**
     * 状态（0正常 1停用）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
