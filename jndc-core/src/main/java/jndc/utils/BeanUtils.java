package jndc.utils;

public class BeanUtils {


    public static <T>T copyValue(Object from,Class<T> tClass){
        String s = JSONUtils.object2JSONString(from);
        T t = JSONUtils.str2Object(s, tClass);
        return t;
    }
}
