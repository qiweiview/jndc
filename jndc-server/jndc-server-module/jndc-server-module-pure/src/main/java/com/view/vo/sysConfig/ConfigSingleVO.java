package com.view.vo.sysConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-02 15:21
 * @description: 单个配置对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigSingleVO {
    /**
     * 参数主键
     */
    private Integer id;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 是否系统内置（0否1是）
     */
    private Integer type;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String remark;

}
