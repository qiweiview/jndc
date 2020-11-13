package web.core;

import web.mapping.DevelopDebugMapping;
import web.mapping.ServerManageMapping;
import jndc.utils.JSONUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * singleton
 */
public class MappingRegisterCenter {
    private Map<String, MappingMethodDescription> mappingMap = new HashMap<>();



    public MappingRegisterCenter() {
        doInit();
    }

    /**
     * register jndc mapping
     */
    private void doInit() {
        registerMapping(new ServerManageMapping());
        registerMapping(new DevelopDebugMapping());

    }


    public void registerMapping(Object mappingBean) {
        if (mappingBean == null) {
            throw new RuntimeException("unSupport null register");
        }
        Class<?> aClass = mappingBean.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        Stream.of(declaredMethods).forEach(x -> {
            WebMapping annotation = x.getAnnotation(WebMapping.class);
            if (annotation != null) {
                String path = annotation.path();
                if (mappingMap.containsKey(path)) {
                    throw new RuntimeException("duplicate path");
                }
                MappingMethodDescription mappingMethodDescription = new MappingMethodDescription(annotation, x, mappingBean);
                mappingMap.put(path, mappingMethodDescription);
            }

        });

    }

    public byte[] invokeMapping(JNDCHttpRequest jndcHttpRequest) {
        StringBuilder fullPath = jndcHttpRequest.getFullPath();
        MappingMethodDescription mappingMethodDescription = mappingMap.get(fullPath.toString());
        if (mappingMethodDescription == null) {
            return null;
        }
        return mappingMethodDescription.invoke(jndcHttpRequest);
    }


    public class MappingMethodDescription {
        private WebMapping webMapping;
        private Method method;
        private Object object;

        public MappingMethodDescription(WebMapping webMapping, Method method, Object object) {
            this.webMapping = webMapping;
            this.method = method;
            this.object = object;
        }

        public byte[] invoke(JNDCHttpRequest jndcHttpRequest) {
            try {
                Object invoke = method.invoke(object, jndcHttpRequest);

                if (invoke instanceof byte[]) {
                    return (byte[]) invoke;
                } else if (invoke instanceof String) {
                    return invoke.toString().getBytes();
                } else {
                    return JSONUtils.object2JSON(invoke);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public WebMapping getWebMapping() {
            return webMapping;
        }

        public void setWebMapping(WebMapping webMapping) {
            this.webMapping = webMapping;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }


}
