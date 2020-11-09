package web.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {


    public static byte[] object2JSON(Object invoke) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsBytes(invoke);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
