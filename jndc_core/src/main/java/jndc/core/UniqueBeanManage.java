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

        //fix tag
//        registerBean(new NDCServerConfigCenter());
//        registerBean(new JNDCClientConfigCenter());
//        registerBean(new IpChecker());
//        registerBean(new MappingRegisterCenter());
//        registerBean(new DataStore());
//        registerBean(new AsynchronousEventCenter());
//        registerBean(new MessageNotificationCenter());
//        registerBean(new ScheduledTaskCenter());

    }


    public static void registerBean(Object o) {
        if (null == o) {
            return;
        }
        Class<?> aClass = o.getClass();
        map.put(aClass, o);
    }
}
