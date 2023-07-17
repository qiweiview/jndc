package jndc.core.data_store_support;


import jndc.core.UniqueBeanManage;
import jndc.utils.JSONUtils;
import jndc.utils.ReflectionCache;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库操作装饰器
 *
 * @param <T>
 */
@Data
public class DBWrapper<T> implements DBOperationI<T> {

    private static final Map<Class, DBWrapper> dBWrapperCache = new ConcurrentHashMap<>();

    private Class tClass;

    private String tableName;

    private String primaryName;

    private Field primaryFile;

    private StringBuilder keyString = new StringBuilder();

    private String keyString4Update = "";

    private String valueString = "";


    private Map<String, Field> filedMap = new HashMap<>();

    private Map<String, String> aliasMap = new HashMap<>();

    private List<String> keys = new ArrayList<>();

    private String insert;

    private String update;

    private String delete;

    private String selectAll;

    private String count;

    public DBWrapper(Class<T> t) {
        this.tClass = t;

        //解析类
        parseClass();

        //创建sql
        createSql();
    }

    /**
     * 创建sql
     */
    private void createSql() {
        insert = "insert into " + tableName + " (" + keyString + ") values(" + valueString + ");";

        delete = "delete from " + tableName + " where " + primaryName + " = ?;";

        selectAll = "select " + keyString + " from " + tableName + ";";

        update = "update " + tableName + " set " + keyString4Update + " where " + primaryName + " = ?;";

        count = "select count(*) count from " + tableName + " ;";
    }

    /**
     * 解析类
     */
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
                markAsKey(x, annotation);
                return;
            }
            DSFiled annotation1 = x.getAnnotation(DSFiled.class);
            markAsFiled(x, annotation1);
        });
        keyString.append(primaryName);
        keyString4Update = keyString4Update + primaryName + "=?";
        valueString = valueString + "?";
        keys.add(primaryName);
        filedMap.put(primaryName, primaryFile);
    }

    /**
     * 标记为key
     *
     * @param x
     * @param annotation
     */
    private void markAsKey(Field x, DSKey annotation) {
        primaryName = x.getName();
        String name = annotation.name();
        if (name.length() > 0) {
            primaryName = name;
        }
        primaryFile = x;

    }

    /**
     * 标记为属性
     *
     * @param x
     * @param annotation
     */
    private void markAsFiled(Field x, DSFiled annotation) {
        String name = x.getName();
        x.setAccessible(true);

        if (annotation != null) {
            if (!annotation.useFiled()) {
                return;
            }
            String overWriteName = annotation.name();
            if (overWriteName.length() > 0) {
                aliasMap.put(overWriteName, name);
                name = overWriteName;
            }
        }
        keyString.append(name + ",");

        keyString4Update += name + "=?,";

        valueString += "?" + ",";
        filedMap.put(name, x);
        keys.add(name);
    }


    @Override
    public void insert(T t) {
        DataStoreAbstract dataStore = UniqueBeanManage.getBean(DataStoreAbstract.class);
        List<String> keys = getKeys();
        Object[] objects = new Object[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            objects[i] = getFiledValue(keys.get(i), t);
        }
        dataStore.execute(getInsert(), objects);
    }

    @Override
    public void insertBatch(List<T> t) {
        if (t != null && t.size() > 0) {
            t.forEach(x -> {
                insert(x);
            });
        }

    }

    @Override
    public void updateByPrimaryKey(T t) {
        DataStoreAbstract dataStore = UniqueBeanManage.getBean(DataStoreAbstract.class);
        List<String> keys = getKeys();
        Object[] objects = new Object[keys.size() + 1];
        for (int i = 0; i < keys.size(); i++) {
            objects[i] = getFiledValue(keys.get(i), t);
        }
        objects[keys.size()] = getFiledValue(primaryName, t);
        dataStore.execute(getUpdate(), objects);
    }


    @Override
    public List<T> listAll() {
        DataStoreAbstract dataStore = UniqueBeanManage.getBean(DataStoreAbstract.class);
        List<Map> maps = dataStore.executeQuery(getSelectAll(), null, aliasMap);
        return parseResult(maps);
    }

    @Override
    public List<T> customQuery(String sql, Object... params) {
        DataStoreAbstract dataStore = UniqueBeanManage.getBean(DataStoreAbstract.class);
        List<Map> maps = dataStore.executeQuery(sql, params, aliasMap);
        return parseResult(maps);
    }

    @Override
    public PageResult<T> customQueryByPage(String sql, int page, int rows, Object... params) {

        PageResult<T> pageResult = new PageResult();
        //min limit
        if (page < 1) {
            page = 1;
        }
        if (rows < 1) {
            rows = 1;
        }

        //max limit
        if (rows > 50) {
            rows = 50;
        }


        int noOfRows = (page - 1) * rows;
        String newSql = "select * from (" + sql + ") g limit " + noOfRows + "," + rows;
        List<T> list = customQuery(newSql, params);
        pageResult.setData(list);
        Number count = customQuerySingleValue("count", "select count(*) as count from (" + sql + ") g", Number.class);
        pageResult.setTotal(count.intValue());
        return pageResult;
    }

    @Override
    public Integer count() {
        DataStoreAbstract dataStore = UniqueBeanManage.getBean(DataStoreAbstract.class);
        List<Map> maps = dataStore.executeQuery(count, null);
        Map map = maps.get(0);
        Integer count = (Integer) map.get("count");
        return count;
    }


    @Override
    public T customQuerySingle(String sql, Object... params) {
        DataStoreAbstract dataStore = UniqueBeanManage.getBean(DataStoreAbstract.class);
        List<Map> maps = dataStore.executeQuery(sql, params, aliasMap);
        List<T> list = parseResult(maps);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public <V> V customQuerySingleValue(String valueKey, String sql, Class<V> f, Object... params) {
        Object defaultValue = null;
        if (Integer.class == f) {
            defaultValue = 0;
        }
        if (String.class == f) {
            defaultValue = "";
        }

        DataStoreAbstract dataStore = UniqueBeanManage.getBean(DataStoreAbstract.class);
        List<Map> maps = dataStore.executeQuery(sql, params, aliasMap);
        if (maps.size() > 0) {
            Object o = maps.get(0).get(valueKey);
            if (o == null) {
                o = defaultValue;
            }
            return (V) o;
        }
        return null;
    }

    @Override
    public void customExecute(String sql, Object... params) {
        DataStoreAbstract dataStore = UniqueBeanManage.getBean(DataStoreAbstract.class);
        dataStore.execute(sql, params);
    }

    @Override
    public void deleteByPrimaryKey(T t) {
        DataStoreAbstract dataStore = UniqueBeanManage.getBean(DataStoreAbstract.class);
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

    private Object getFiledValue(String filed, Object o) {
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


    public static <T> DBWrapper<T> getDBWrapper(Class<? extends T> tClass) {
        if (tClass == null) {
            throw new RuntimeException("unSupport null");
        }

        DBWrapper dbWrapper = dBWrapperCache.get(tClass);
        if (dbWrapper == null) {
            dbWrapper = new DBWrapper(tClass);
            dBWrapperCache.put(tClass, dbWrapper);
        }
        return dbWrapper;
    }

    public static <T> DBWrapper<T> getDBWrapper(T t) {
        if (t == null) {
            throw new RuntimeException("unSupport null");
        }
        Class<T> aClass = (Class<T>) t.getClass();
        return getDBWrapper(aClass);
    }

}
