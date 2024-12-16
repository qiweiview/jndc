package com.view.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * (SysNoticeUserRead)表实体类
 *
 * @author sjh
 * @since 2024-08-16 14:27:18
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysNoticeUserRead  {
    /**
    * 主键
    */
    @TableId
    private Long id;

    /**
    * 通知公告ID
    */
    private Long noticeId;

    /**
    * 用户ID
    */
    private Long userId;

    /**
    * 状态是否已读（0否1是）
    */
    private Integer status;

    /**
    * 已读时间
    */
    private LocalDateTime readTime;

    /**
    * 创建时间
    */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
    * 更新时间
    */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
    * 是否删除（0否1是）
     * 暂不做软删除，而是直接删除
    */
    private Integer delFlag;


}

