package jndc.core.data_store;


import jndc.core.UniqueBeanManage;
import jndc.utils.JSONUtils;
import jndc.utils.LogPrint;
import jndc.utils.ReflectionCache;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DBWrapper<T> implements BasicDatabaseOperations<T> {

    private static final Map<Class, DBWrapper> dBWrapperCache = new ConcurrentHashMap<>();

    private Class tClass;

    private String tableName;

    private String primaryName;

    private Field primaryFile;

    private String keyString = "";

    private String valueString = "";


    private Map<String, Field> filedMap = new HashMap<>();

    private List<String> keys = new ArrayList<>();

    private String insert;

    private String delete;

    private String selectAll;

    public DBWrapper(Class<T> t) {
        this.tClass = t;
        parseClass();
        createSql();
    }

    private void createSql() {
        insert = "insert into " + tableName + " (" + keyString + ") values(" + valueString + ");";

        delete = "delete from " + tableName + " where " + primaryName + " = ?;";

        selectAll = "select " + keyString + " from " + tableName + ";";
    }

    private void parseClass() {
        DSTable annotation2 = (DSTable) tClass.getAnnotation(DSTable.class);
        if (annotation2 == null) {
            throw new RuntimeException("unSupport class,must have @DSTable annotation");
        }

        tableName = tClass.getSimpleName();
        if (annotation2 != null) {
            tableName = annotation2.name();
        }


        List<Field> fields = ReflectionCache.getFields(tClass);
        fields.forEach(x -> {
            DSKey annotation = x.getAnnotation(DSKey.class);
            if (annotation != null) {
                isKey(x, annotation);
                return;
            }
            DSFile annotation1 = x.getAnnotation(DSFile.class);
            isFile(x, annotation1);
        });
        keyString = keyString + primaryName;
        valueString = valueString + "?";
        keys.add(primaryName);
        filedMap.put(primaryName, primaryFile);
    }

    private void isKey(Field x, DSKey annotation) {
        primaryName = x.getName();
        String name = annotation.name();
        if (name.length() > 0) {
            primaryName = name;
        }
        primaryFile = x;

    }

    private void isFile(Field x, DSFile annotation) {
        String name = x.getName();
        x.setAccessible(true);

        if (annotation != null) {
            if (!annotation.useFile()) {
                return;
            }
            String name1 = annotation.name();
            if (name1.length() > 0) {
                name = name1;
            }
        }
        keyString += name + ",";
        valueString += "?" + ",";
        filedMap.put(name, x);
        keys.add(name);
    }


    @Override
    public void insert(T t) {
        DataStore dataStore = UniqueBeanManage.getBean(DataStore.class);

        List<String> keys = getKeys();
        Object[] objects = new Object[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            objects[i] = getFiledValue(keys.get(i), t);
        }
        dataStore.execute(getDelete(), objects);
    }


    @Override
    public List<T> listAll() {
        DataStore dataStore = UniqueBeanManage.getBean(DataStore.class);
        List<Map> maps = dataStore.executeQuery(getSelectAll(), null);
        return parseResult(maps);
    }

    @Override
    public List<T> customQuery(String sql, Object... params) {
        DataStore dataStore = UniqueBeanManage.getBean(DataStore.class);
        List<Map> maps = dataStore.executeQuery(sql, params);
        return parseResult(maps);
    }

    @Override
    public void customExecute(String sql, Object... params) {
        DataStore dataStore = UniqueBeanManage.getBean(DataStore.class);
        dataStore.execute(sql, params);
    }

    @Override
    public void deleteByPrimaryKey(T t) {
        DataStore dataStore = UniqueBeanManage.getBean(DataStore.class);
        Object filedValue = getFiledValue(primaryName, t);
        Object[] d = {filedValue};
        dataStore.execute(getDelete(), d);
    }

    private List<T> parseResult(List<Map> list) {
        if (list.size() < 1) {
            return new ArrayList<>();
        }
        String s = JSONUtils.object2JSONString(list);
        List<T> t = JSONUtils.str2ObjectArray(s, tClass);
        return t;

    }

    public Object getFiledValue(String filed, Object o) {
        Field field = filedMap.get(filed);
        try {
            field.setAccessible(true);
            Object o1 = field.get(o);
            return o1;
        } catch (Exception e) {
            throw new RuntimeException("get filed value fail: " + e);
        }


    }

    /* ========================getter setter======================== */

    public String getInsert() {
        return insert;
    }

    public String getDelete() {
        return delete;
    }

    public List<String> getKeys() {
        return keys;
    }

    public String getSelectAll() {
        return selectAll;
    }

    public static <T> DBWrapper<T> getDBWrapper(T t) {
        if (t == null) {
            throw new RuntimeException("unSupport null");
        }
        Class<?> aClass = t.getClass();
        DBWrapper dbWrapper = dBWrapperCache.get(aClass);
        if (dbWrapper == null) {
            dbWrapper = new DBWrapper(t.getClass());
        }
        return dbWrapper;
    }

}
