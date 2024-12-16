package com.view.event;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录事件
 */

@Data
public class LoginLogEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 用户账号
     */
    private String account;

    /**
     * 登录IP地址
     */
    private String ipAddress;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录状态（0成功 1失败）
     */
    private Integer status;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

}
