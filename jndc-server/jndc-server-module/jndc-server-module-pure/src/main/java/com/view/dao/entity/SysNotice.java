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
 * 通知公告表(SysNotice)表实体类
 *
 * @author sjh
 * @since 2024-08-04 20:22:25
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysNotice  {
    /**
    * 公告ID
    */
    @TableId
    private Long id;

    /**
    * 公告标题
    */
    private String title;

    /**
    * 公告内容
    */
    private String content;

    /**
    * 公告类型（1通知 2公告）
    */
    private Integer type;

    /**
    * 公告状态（0正常 1关闭）
    */
    private Integer status;

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

