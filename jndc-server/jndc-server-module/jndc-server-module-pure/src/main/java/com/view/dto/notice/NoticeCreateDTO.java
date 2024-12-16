package com.view.dto.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-15 14:35
 * @description: 通知公告创建
 */
@Data
public class NoticeCreateDTO {

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
     * 通知角色ID
     */
    private Set<Long> roleIds;
}
