package com.view.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 参数配置表(SysConfig)表实体类
 *
 * @author sjh
 * @since 2024-08-02 14:22:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysConfig  {
    /**
    * 参数主键
    */
    @TableId
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
    * 更新者
    */
    private Long updateBy;

    /**
    * 更新时间
    */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
    * 是否删除 0否1是
    */
    @TableLogic
    private Integer delFlag;

    /**
    * 备注
    */
    private String remark;




}

