package jndc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Reflection Cache Utils
 */
public class ReflectionCache {
    private static Map<Class, ClassCache> map = new ConcurrentHashMap<>();

    public static List<Method> getMethods(Class tClass) {
        return getClassCache(tClass).getMethods();

    }

    public static ClassCache getClassCache(Class tClass) {
        ClassCache classCache = map.get(tClass);
        if (classCache == null) {
            classCache = initClassCache(tClass);
        }
        return classCache;
    }

    public static List<Field> getFields(Class tClass) {

        return getClassCache(tClass).getFieldList();

    }

    private static ClassCache initClassCache(Class tc) {
        ClassCache classCache = new ClassCache();
        Stream.of(tc.getDeclaredFields()).forEach(x -> {
            classCache.addField(x);
        });
        Stream.of(tc.getDeclaredMethods()).forEach(x -> {
            classCache.addMethod(x);
        });
        classCache.setInnerClass(tc);


        map.put(tc, classCache);
        return classCache;
    }


    public static class ClassCache {
        private Class innerClass;
        private Class arrayClass;
        private List<Field> fieldList = new ArrayList<>();
        private List<Method> methods = new ArrayList<>();
        private Map<String, Field> fieldMap = new HashMap<>();
        private Map<String, Method> methodMap = new HashMap<>();


        public void addField(Field field) {
            fieldMap.put(field.getName(), field);
            fieldList.add(field);
        }

        public void addMethod(Method method) {
            methodMap.put(method.getName(), method);
            methods.add(method);
        }

        public void createArrayType() {
            if (!innerClass.isArray()) {
                try {
                    arrayClass = Class.forName("[L" + innerClass.getCanonicalName() + ";");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("class cache create error: " + e);
                }
            }
        }


        public Class getArrayClass() {
            return arrayClass;
        }

        public void setArrayClass(Class arrayClass) {
            this.arrayClass = arrayClass;
        }

        public Class getInnerClass() {
            return innerClass;
        }

        public void setInnerClass(Class innerClass) {
            this.innerClass = innerClass;
            createArrayType();
        }

        public List<Field> getFieldList() {
            return fieldList;
        }

        public void setFieldList(List<Field> fieldList) {
            this.fieldList = fieldList;
        }

        public List<Method> getMethods() {
            return methods;
        }

        public void setMethods(List<Method> methods) {
            this.methods = methods;
        }

        public Map<String, Field> getFieldMap() {
            return fieldMap;
        }

        public void setFieldMap(Map<String, Field> fieldMap) {
            this.fieldMap = fieldMap;
        }

        public Map<String, Method> getMethodMap() {
            return methodMap;
        }

        public void setMethodMap(Map<String, Method> methodMap) {
            this.methodMap = methodMap;
        }
    }
}
