package com.view.dto.dictData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-06-29 15:29
 * @description: 字典数据项修改对象
 */
@Data
public class SysDictDataUpdateDTO {

    /**
     * 字典数据项ID
     */
    @NotNull(message = "字典数据项ID不能为空")
    private Long id;

    /**
     * 字典ID
     */
    @NotNull(message = "字典ID不能为空")
    private Long dictId;

    /**
     * 数据项名称
     */
    @NotBlank(message = "数据项名称不能为空")
    private String name;

    /**
     * 数据项值
     */
    @NotBlank(message = "数据项值不能为空")
    private String value;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

    /**
     * 颜色值
     */
    private String color;


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
