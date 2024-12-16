package com.view.vo;

import com.view.vo.menu.AsyncRoutesVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-07 16:45
 * @description: 用户登录成功返回对象
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO {

    /**
     * token值
     */
    private String accessToken;

    /**
     * id
     */
    private Long id;


    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 简介
     */
    private String intro;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDateTime birthday;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * IP来源
     */
    private String ipSource;

    /**
     * 角色集合
     */
    private List<String> roles;

    /**
     * 菜单列表
     */
    private List<AsyncRoutesVO> asyncRoutesVOList;
}
