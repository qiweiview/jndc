package jndc.core;


import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * simple object management,can use spring or other tools replace
 */
@Slf4j
@Deprecated
public class UniqueBeanManage {

    private static Map<Class, Object> map = new ConcurrentHashMap<>();

    public static <T> T getBean(Class<T> tClass) {
        Object o = map.get(tClass);
        if (o == null) {
            log.error("can not find the bean " + tClass);
            throw new RuntimeException("no matching bean "+tClass);
        }
        return (T) o;
    }





    public static void registerBean(Object o) {
        registerBean(o.getClass(), o);
    }

    public static void registerBean(Class tClass, Object o) {

        Object o1 = map.get(tClass);
        if (o1 != null) {
            throw new RuntimeException("exist a bean " + o1);
        }

        map.put(tClass, o);
    }
}
