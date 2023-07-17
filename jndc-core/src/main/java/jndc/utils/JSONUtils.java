package jndc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JSONUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static byte[] object2JSON(Object invoke) {

        try {
            return objectMapper.writeValueAsBytes(invoke);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String object2JSONString(Object invoke) {

        try {
            return objectMapper.writeValueAsString(invoke);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T str2Object(String str, Class<T> tClass) {
        try {
            return objectMapper.readValue(str, tClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("deserialization fail ,cause" + e);
            throw new RuntimeException(e);
        }
    }


    public static <T> List<T> str2ObjectArray(String s, Class<T> tClass) {
        Class arrayClass = ReflectionCache.getClassCache(tClass).getArrayClass();
        try {
            T[] array = (T[]) objectMapper.readValue(s, arrayClass);
            List<T> list = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
            return list;
        } catch (JsonProcessingException e) {
            log.error("deserialization array fail ,cause", e);
            throw new RuntimeException(e);
        }
    }
}
