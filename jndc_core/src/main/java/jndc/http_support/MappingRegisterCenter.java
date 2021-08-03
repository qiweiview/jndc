package jndc.http_support;


import jndc.http_support.model.MappingMethodDescription;
import jndc.http_support.model.NettyRequest;
import jndc.http_support.model.ResponseDescription;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * singleton
 */
@Slf4j
public class MappingRegisterCenter {
    private Map<String, MappingMethodDescription> mappingMap = new HashMap<>();


    public void registerMapping(Class... tClass) {
        Stream.of(tClass).forEach(x -> {
            try {
                Constructor constructor = x.getConstructor();
                Object o = constructor.newInstance();
                registerMapping(o);
            } catch (Exception e) {
                throw new RuntimeException("register " + x + " fail cause" + e);
            }

        });
    }


    private void registerMapping(Object mappingBean) {
        if (mappingBean == null) {
            throw new RuntimeException("unSupport null register");
        }
        Class<?> aClass = mappingBean.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        Stream.of(declaredMethods).forEach(x -> {
            WebMapping annotation = x.getAnnotation(WebMapping.class);
            if (annotation != null) {
                String path = annotation.value();
                if (mappingMap.containsKey(path)) {
                    throw new RuntimeException("duplicate path");
                }
                MappingMethodDescription mappingMethodDescription = new MappingMethodDescription(annotation, x, mappingBean);
                mappingMap.put(path, mappingMethodDescription);
            }

        });

    }

    public ResponseDescription invokeMapping(NettyRequest nettyRequest) {
        StringBuilder fullPath = nettyRequest.getFullPath();
        MappingMethodDescription mappingMethodDescription = mappingMap.get(fullPath.toString());
        if (mappingMethodDescription == null) {
            return null;
        }

        try {
            WebMapping.RESPONSE_TYPE type = mappingMethodDescription.getWebMapping().type();
            ResponseDescription responseDescription = new ResponseDescription();
            responseDescription.setData(mappingMethodDescription.invoke(nettyRequest));
            responseDescription.setResponseType(type);
            return responseDescription;
        } catch (RuntimeException e) {
            ResponseDescription responseDescription = new ResponseDescription();
            responseDescription.setData(e.getMessage().getBytes());
            return responseDescription;
        }

    }


}
