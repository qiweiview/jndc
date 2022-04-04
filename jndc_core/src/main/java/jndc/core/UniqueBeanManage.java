package jndc.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * simple object management,can use spring or other tools replace
 */
public class UniqueBeanManage {
    private static final Logger logger = LoggerFactory.getLogger(UniqueBeanManage.class);
    private static Map<Class, Object> map = new ConcurrentHashMap<>();

    public static <T> T getBean(Class<T> tClass) {
        Object o = map.get(tClass);
        if (o == null) {
            logger.error("can not find the bean "+tClass);
            throw new RuntimeException("no matching bean "+tClass);
        }
        return (T) o;
    }


    static {


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
