package jndc.core.data_store_support;

import java.util.List;

/**
 * 基础数据库操作
 *
 * @param <T>
 */
public interface DBOperationI<T> {
    /**
     * 插入
     *
     * @param t
     */
    public void insert(T t);

    /**
     * 批量新增
     *
     * @param t
     */
    public void insertBatch(List<T> t);

    /**
     * 主键更新
     *
     * @param t
     */
    public void updateByPrimaryKey(T t);

    /**
     * 主键删除
     *
     * @param t
     */
    public void deleteByPrimaryKey(T t);

    /**
     * 查询所有
     *
     * @return
     */
    public List<T> listAll();

    /**
     * 自定义查询
     *
     * @param sql
     * @param params
     * @return
     */
    public List<T> customQuery(String sql, Object... params);

    /**
     * 自定义分页查询
     *
     * @param sql
     * @param page
     * @param rows
     * @param params
     * @return
     */
    public PageResult<T> customQueryByPage(String sql, int page, int rows, Object... params);

    /**
     * 计数
     *
     * @return
     */
    public Integer count();

    /**
     * 自定义查询单对象
     *
     * @param sql
     * @param params
     * @return
     */
    public T customQuerySingle(String sql, Object... params);

    /**
     * 自定义查询单值
     *
     * @param valueKey
     * @param sql
     * @param f
     * @param params
     * @param <V>
     * @return
     */
    public <V> V customQuerySingleValue(String valueKey, String sql, Class<V> f, Object... params);

    /**
     * 自定义检索
     *
     * @param sql
     * @param params
     */
    public void customExecute(String sql, Object[] params);
}
