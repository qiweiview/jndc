package jndc.core;


import jndc.client.JNDCClientConfigCenter;
import jndc.server.NDCServerConfigCenter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * single bean init,can use spring or other tools replace
 */
public class UniqueBeanManage {

    private static Map<Class, Object> map = new ConcurrentHashMap<>();

    public static <T> T getBean(Class<T> tClass) {
        Object o = map.get(tClass);
        if (o == null) {
            throw new RuntimeException("no matching bean ");
        }
        return (T) o;
    }


    static {
        registerBean(new NDCServerConfigCenter());
        registerBean(new JNDCClientConfigCenter());
    }


    private static void registerBean(Object o) {
        if (null == o) {
            return;
        }
        Class<?> aClass = o.getClass();
        map.put(aClass, o);
    }
}
