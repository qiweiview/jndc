package com.view.vo.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-12 9:33
 * @description: 用户
 */
@Data
public class SysUserVO {
    /**
     * 主键（用户id）
     */
    private Long id;

    private String weChatId;

    /**
     * 用户名（登录名）
     */
    private String username;


    /**
     * 邮箱号
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 简介
     */
    private String intro;

    /**
     * 性别（0未知，1男，2女）
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDateTime birthday;

    /**
     * 状态（0正常，1禁用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createBy;


    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP地址
     */
    private String ipAddress;

    /**
     * 最后登录IP来源
     */
    private String ipSource;

    /**
     * 注册来源
     */
    private String registerSource;

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
