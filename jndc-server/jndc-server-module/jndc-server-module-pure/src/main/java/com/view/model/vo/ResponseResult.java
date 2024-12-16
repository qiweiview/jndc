package com.view.model.vo;

import com.view.enums.StatusCodeEnum;
import lombok.Getter;

import java.io.Serializable;

/**
 * @Author sjh
 * @Date 2023/1/14 13:35
 * @Description: 统一返回数据格式封装
 * @Version 1.0
 */
@Getter
public class ResponseResult<T> implements Serializable {

    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    private ResponseResult() {
        this.code = StatusCodeEnum.SUCCESS.getCode();
        this.message = StatusCodeEnum.SUCCESS.getMessage();
    }

    private ResponseResult(StatusCodeEnum statusCodeEnum) {
        this.code = statusCodeEnum.getCode();
        this.message = statusCodeEnum.getMessage();
    }

    private ResponseResult(String message) {
        this.code = StatusCodeEnum.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    private ResponseResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private ResponseResult(T data) {
        this();
        this.data = data;
    }

    private ResponseResult(String message, T data) {
        this.code = StatusCodeEnum.SUCCESS.getCode();
        this.message = message;
        this.data = data;
    }

    /**
     * 业务处理成功,无数据返回
     */
    public static ResponseResult<Void> ok() {
        return new ResponseResult<>();
    }

    /**
     * 业务处理成功，有数据返回
     */
    public static <T> ResponseResult<T> ok(T data) {
        return new ResponseResult<>(data);
    }

    /**
     * 业务处理成功，有数据以及成功的具体信息返回
     */

    public static <T> ResponseResult<T> ok(String message, T data) {
        return new ResponseResult<>(message, data);
    }

    /**
     * 业务处理失败
     */
    public static ResponseResult<Void> fail(StatusCodeEnum errorCode) {
        return new ResponseResult<>(errorCode);
    }

    /**
     * 业务处理失败，返回失败码以及失败信息
     */
    public static ResponseResult<Void> fail(Integer code, String message) {
        return new ResponseResult<>(code, message);
    }

    /**
     * 系统错误
     */
    public static ResponseResult<Void> error() {
        return new ResponseResult<>(StatusCodeEnum.SYSTEM_ERROR);
    }


    /**
     * 系统详细错误
     */
    public static ResponseResult<Void> error(String message) {
        return new ResponseResult<>(message);
    }
}
