package jndc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JSONUtils {
    private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

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
            logger.error("deserialization fail ,cause"+e);
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
            logger.error("deserialization array fail ,cause"+e);
            throw new RuntimeException(e);
        }
    }
}
