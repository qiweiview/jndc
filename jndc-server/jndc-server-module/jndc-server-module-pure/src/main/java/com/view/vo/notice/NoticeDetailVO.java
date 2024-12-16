package com.view.vo.notice;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-16 10:10
 * @description: 通知详情
 */
@Data
public class NoticeDetailVO {
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
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 通知的角色
     */
    private String roleNameArr;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 通知角色ID
     */
    private Set<Long> roleIds;
}
