package com.view.vo.notice;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-17 15:07
 * @description: 用户通知公告
 */
@Data
public class NoticeUserVO {

    /**
     * 公告ID
     */
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
     * 是否已读
     */
    private Integer readStatus;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;
}
