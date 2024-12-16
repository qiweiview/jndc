package com.view.dto.sysConfig;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-02 14:50
 * @description: 系统配置创建对象
 */
@Data
public class SysConfigCreateDTO {

    /**
     * 参数名称
     */
    @NotBlank(message = "参数名称不能为空")
    private String configName;

    /**
     * 参数键名
     */
    @NotBlank(message = "参数键名不能为空")
    private String configKey;

    /**
     * 参数键值
     */
    @NotBlank(message = "参数键值不能为空")
    private String configValue;

    /**
     * 备注
     */
    private String remark;

}
