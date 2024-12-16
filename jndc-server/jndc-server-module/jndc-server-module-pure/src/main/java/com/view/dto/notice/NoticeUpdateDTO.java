package com.view.dto.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-15 16:15
 * @description: 通知公告修改
 */
@Data
public class NoticeUpdateDTO {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 公告标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 公告内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 公告类型（1通知 2公告）
     */
    @NotNull(message = "公告类型不能为空")
    private Integer type;

    /**
     * 公告状态（0正常 1关闭）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 通知角色ID（暂时不让修改）
     */
//    private Set<Long> roleIds;

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
