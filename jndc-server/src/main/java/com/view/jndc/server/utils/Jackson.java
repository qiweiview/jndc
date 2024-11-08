package com.view.jndc.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Jackson {


    public static ObjectMapper objectMapper = new ObjectMapper();


    public static byte[] toJsonByte(Object o) {
        try {
            return objectMapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("to json fail cause:" + e.getMessage());
        }
    }

    /**
     * 对象转字符串
     *
     * @param o
     * @return
     */
    public static String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("to json fail cause:" + e.getMessage());
        }
    }

    /**
     * 字符串转对象
     *
     * @param o
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T toObject(String o, Class<T> tClass) {
        try {
            return objectMapper.readValue(o, tClass);
        } catch (Exception e) {
            throw new RuntimeException("对象转换失败：" + e.getMessage());
        }
    }


}
