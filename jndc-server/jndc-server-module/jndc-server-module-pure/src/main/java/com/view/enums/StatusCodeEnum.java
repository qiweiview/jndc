package com.view.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sjh
 * @date 2023-1-14 13:51
 * @description: 系统各种状态枚举
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum StatusCodeEnum {
    /**
     * 成功
     */
    SUCCESS(0, "请求成功"),

    /**
     * 验证码错误
     */
    VERIFICATIONCODE_ERROR(400, "验证码错误"),

    /**
     * 未登录
     */
    NO_LOGIN(401, "用户未登录"),
    /**
     * 没有操作权限
     */
    AUTHORIZED(403, "无操作权限"),

    /**
     * 404
     */
    NO_FOUND(404,"系统无此资源"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(500, "系统错误"),
    /**
     * 操作失败
     */
    FAIL(510, "操作失败"),
    /**
     * 参数格式不正确
     */
    VALID_ERROR(520, "参数校验失败"),
    /**
     * 上传失败
     */
    UPLOAD_FAIL(530, "上传失败"),
    /**
     * 创建目录失败
     */
    CREATE_MKR_FAIL(540, "创建目录失败"),
    /**
     * 用户已存在
     */
    USER_EXIST(610, "用户已存在"),
    /**
     * 用户不存在
     */
    USER_NOT_EXIST(620, "用户不存在"),

    /**
     * 账号已被冻结
     */
    USER_FREEZE(630,"账号已被冻结"),

    /**
     * 账号无权限进入后台
     */
    USER_NO_ACCESS(640,"此账号无权限进入后台"),

    /**
     * 角色已被冻结
     */
    ROLE_FREEZE(630,"角色已被禁用"),


    /**
     * 常规登录错误
     */
    LOGIN_ERROR(700, "用户名或密码错误"),
    /**
     * qq登录错误
     */
    QQ_LOGIN_ERROR(710, "qq登录错误"),
    /**
     * 微博登录错误
     */
    WEIBO_LOGIN_ERROR(720, "微博登录错误"),

    /**
     * token验证错误
     */
    TOKEN_ERROR(800, "token验证错误");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String message;

}
