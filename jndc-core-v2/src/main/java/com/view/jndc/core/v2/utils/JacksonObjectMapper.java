package com.view.jndc.core.v2.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonObjectMapper {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String serialize(Object o) {
        if (o == null) {
            throw new RuntimeException("对象为空");
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] serialize2ByteArray(Object o) {
        if (o == null) {
            throw new RuntimeException("对象为空");
        }
        try {
            return OBJECT_MAPPER.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(String data, Class<T> tClass) {
        if (data == null || tClass == null) {
            throw new RuntimeException("数据为空");
        }
        try {
            return OBJECT_MAPPER.readValue(data, tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
