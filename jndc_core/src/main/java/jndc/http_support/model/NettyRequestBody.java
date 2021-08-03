package jndc.http_support.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jndc.utils.Jackson;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class NettyRequestBody {

    /**
     * 数值键值对
     */
    private Map<String, List<Object>> attributeValueMap = new HashMap<>();

    /**
     * 文件键值对
     */
    private Map<String, List<FormDataFile>> fileValueMap = new HashMap<>();

    public void addAttributeValue(String jsonString) {
        Map<String, String> map = Jackson.toObject(jsonString, Map.class);
        map.forEach((k, v) -> {
            addAttributeValue(k, v);
        });
    }

    public <T> T toObject(Class<T> tClass) {
        ObjectNode objectNode = Jackson.createObjectNode();
        attributeValueMap.forEach((k, v) -> {
            objectNode.putPOJO(k, v.get(0));
        });
        return Jackson.nodeToObject(objectNode, tClass);
    }

    public List<FormDataFile> getFiles(String key) {
        return fileValueMap.get(key);
    }


    public void release() {
        attributeValueMap.clear();
        fileValueMap.forEach((k, v) -> {
            v.forEach(x -> {
                x.release();
            });
        });
        fileValueMap.clear();
    }


    public void addAttributeValue(String iv, String value) {
        List<Object> list = attributeValueMap.get(iv);
        if (list == null) {
            list = new ArrayList<>();
            attributeValueMap.put(iv, list);
        }
        list.add(value);

    }

    public void addFileValue(String iv, FormDataFile formDataFile) {
        List<FormDataFile> list = fileValueMap.get(iv);
        if (list == null) {
            list = new ArrayList<>();
            fileValueMap.put(iv, list);
        }
        list.add(formDataFile);
    }
}
