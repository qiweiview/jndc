package jndc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Jackson {


    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectNode createObjectNode() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        return objectNode;
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
        } catch (JsonProcessingException e) {
            throw new RuntimeException("to json fail cause:" + e.getMessage());
        }
    }

    /**
     * 字符串转查找树
     *
     * @param o
     * @return
     */
    public static JsonNode readTree(String o) {
        try {
            return objectMapper.readTree(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("to tree fail cause:" + e.getMessage());
        }
    }


    /**
     * 节点转对象
     *
     * @param o
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T nodeToObject(TreeNode o, Class<T> tClass) {
        try {
            return objectMapper.treeToValue(o, tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("node To Object fail cause:" + e.getMessage());
        }
    }
}
