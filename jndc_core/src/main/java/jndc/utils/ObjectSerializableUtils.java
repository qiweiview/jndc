package jndc.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 对象序列化工具（基于 Jackson JSON，替代原生 Java 序列化以防止反序列化 RCE）
 */
public class ObjectSerializableUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 对象转字节数组
     *
     * @param obj 待序列化对象
     * @return JSON 字节数组
     */
    public static byte[] object2bytes(Object obj) {
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败: " + e.getMessage(), e);
        }
    }


    /**
     * 字节数组转对象
     *
     * @param data  JSON 字节数组
     * @param tClass 目标类型
     * @return 反序列化对象
     */
    public static <T> T bytes2object(byte[] data, Class<T> tClass) {
        try {
            return MAPPER.readValue(data, tClass);
        } catch (Exception e) {
            throw new RuntimeException("反序列化失败: " + e.getMessage(), e);
        }
    }


}
