package com.view.free_lite.common.config.model.base;


import com.view.free_lite.common.emum_value.base.ResultCode;

import com.view.free_lite.common.utils.Jackson;
import lombok.Data;

import java.util.function.Function;

@Data
public class EncryptedResponse {


    private Integer code;

    private String message;

    private Object data = "";


    public <T> T getData(Class<T> tClass) {
        if (getData() == null) {
            throw new RuntimeException("响应为空");
        }
        return Jackson.toObject(Jackson.toJson(data), tClass);
    }


    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static EncryptedResponse success(Object data) {
        EncryptedResponse tEncryptedResponse = new EncryptedResponse();
        tEncryptedResponse.setCode(ResultCode.SUCCESS.getCode());
        tEncryptedResponse.setMessage(ResultCode.SUCCESS.getMessage());
        tEncryptedResponse.setData(data);
        return tEncryptedResponse;
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static EncryptedResponse success(Object data, String message) {
        EncryptedResponse tEncryptedResponse = new EncryptedResponse();
        tEncryptedResponse.setCode(ResultCode.SUCCESS.getCode());
        tEncryptedResponse.setMessage(message);
        tEncryptedResponse.setData(data);
        return tEncryptedResponse;
    }


    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static EncryptedResponse failed(String message) {
        EncryptedResponse tEncryptedResponse = new EncryptedResponse();
        tEncryptedResponse.setCode(ResultCode.FAILED.getCode());
        tEncryptedResponse.setMessage(message);
        tEncryptedResponse.setData("");
        return tEncryptedResponse;
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static EncryptedResponse failed(String message, Integer code) {
        EncryptedResponse tEncryptedResponse = new EncryptedResponse();
        tEncryptedResponse.setCode(code);
        tEncryptedResponse.setMessage(message);
        tEncryptedResponse.setData("");
        return tEncryptedResponse;
    }


    public void encrypt(Function<String, String> consumer) {
        Object data = getData();
        String modify;
        if (data == null) {
            modify = consumer.apply("null");
        } else {
            modify = consumer.apply(data.toString());
        }

        setData(modify);

    }
}
