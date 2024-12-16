package com.view.vo.notice;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-04 20:55
 * @description: 通知公告
 */
@Data
public class NoticeSimpleVO {

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

    private String idString;

    public void setId(Long id) {
        this.id = id;
        if (id != null&&idString==null) {
            this.idString = id.toString();
        }
    }

    public void setIdString(String idString) {
        this.idString = idString;
        if (idString != null) {
            this.id = Long.parseLong(idString);
        }
    }

}
