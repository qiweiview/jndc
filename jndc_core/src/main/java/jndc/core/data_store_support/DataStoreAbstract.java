package jndc.core.data_store_support;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据存储抽象
 */
@Slf4j
public abstract class DataStoreAbstract {

    private volatile boolean initialized = false;


    public void init() {
        if (!initialized) {
            synchronized (DataStoreAbstract.class) {
                flywayInit();
            }
        }

    }


    /**
     * 获取连接
     *
     * @return
     */
    public abstract Connection getConnection();

    /**
     * 初始化flyway
     *
     * @return
     */
    public abstract void flywayInit();

    /**
     * 解析结果
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public List<Map> parseResult(ResultSet rs, Map<String, String> aliasMap) throws SQLException {
        List<Map> list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();//获取键名

        int columnCount = md.getColumnCount();//获取行的数量
        while (rs.next()) {
            Map rowData = new HashMap();//声明Map
            for (int i = 1; i <= columnCount; i++) {
                String columnName = md.getColumnName(i);
                if (aliasMap != null) {
                    String newName = aliasMap.get(columnName);
                    if (newName != null) {
                        columnName = newName;
                    }
                }
                rowData.put(columnName, rs.getObject(i));//获取键名及值
            }
            list.add(rowData);
        }
        return list;
    }

    /**
     * 执行
     *
     * @param sql
     * @param objects
     */
    public void execute(String sql, Object[] objects) {
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {

            if (objects == null) {
                objects = new Object[0];
            }
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            preparedStatement.execute();

        } catch (SQLException sqlException) {
            log.error(sqlException.toString());
            throw new RuntimeException("execute error: " + sqlException);
        }
    }


    /**
     * 查询
     *
     * @param sql
     * @param objects
     */
    public List<Map> executeQuery(String sql, Object[] objects) {

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {

            if (objects == null) {
                objects = new Object[0];
            }
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Map> maps = parseResult(resultSet, null);
                return maps;
            } catch (SQLException exception) {
                throw exception;
            }


        } catch (SQLException sqlException) {
            log.error(sqlException.toString());
            throw new RuntimeException("execute error: " + sqlException);
        }
    }

    /**
     * 查询
     *
     * @param sql
     * @param objects
     */
    public List<Map> executeQuery(String sql, Object[] objects, Map<String, String> aliasMap) {

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {

            if (objects == null) {
                objects = new Object[0];
            }
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Map> maps = parseResult(resultSet, aliasMap);
                return maps;
            } catch (SQLException exception) {
                throw exception;
            }


        } catch (SQLException sqlException) {
            log.error(sqlException.toString());
            throw new RuntimeException("execute error: " + sqlException);
        }
    }


}
