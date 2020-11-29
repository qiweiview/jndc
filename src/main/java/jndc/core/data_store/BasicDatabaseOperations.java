package jndc.core.data_store;

import java.util.List;

public interface BasicDatabaseOperations<T> {
    public void insert(T t);

    public void insertBatch(List<T> t);

    public void updateByPrimaryKey(T t);

    public void deleteByPrimaryKey(T t);

    public List<T> listAll();

    public List<T> customQuery(String sql,Object... params);

    public PageResult<T> customQueryByPage(String sql,int page,int rows,Object... params);

    public Integer count();

    public T customQuerySingle(String sql,Object... params);

    public <V>V customQuerySingleValue(String valueKey,String sql, Class<V> f, Object... params);

    public void customExecute(String sql,Object[] params);
}
