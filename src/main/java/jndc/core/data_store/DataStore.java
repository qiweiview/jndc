package jndc.core.data_store;

import jndc.server.ServerPortBind;
import jndc.utils.UUIDSimple;


import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    private final String SQL_LITE_DB = "jnc_db.db";

    private final String PROTOCOL = "jdbc:sqlite:";

    private volatile boolean initialized = false;

    private Connection connection;

    public DataStore() {

    }

    private void init() {
        if (!initialized) {
            synchronized (this) {
                // UnifiedConfiguration bean = UniqueBeanManage.getBean(UnifiedConfiguration.class);
                //  String runtimeDir = bean.getRuntimeDir();
                String runtimeDir = "C:\\Users\\刘启威\\Desktop\\start_script";

                if (!runtimeDir.endsWith(File.separator)) {
                    runtimeDir += File.separator;
                }

                String s = PROTOCOL + runtimeDir + SQL_LITE_DB;

                try {
                    connection = DriverManager.getConnection(s);
                    initialized = true;
                } catch (SQLException sqlException) {
                    throw new RuntimeException(sqlException);
                }
            }
        }
    }

    public void execute(String sql, Object[] objects) {
        init();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            preparedStatement.execute();

        } catch (SQLException sqlException) {
            throw new RuntimeException("execute error: " + sqlException);
        }
    }

    public List<Map> executeQuery(String sql, Object[] objects) {
        init();
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if (objects == null) {
                objects = new Object[0];
            }
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            resultSet = preparedStatement.executeQuery();
            List<Map> maps = parseResult(resultSet);
            return maps;
        } catch (SQLException sqlException) {
            throw new RuntimeException("execute error: " + sqlException);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException sqlException) {
                throw new RuntimeException("result close fail:" + sqlException);
            }
        }
    }

    private List<Map> parseResult(ResultSet rs) throws SQLException {
        List<Map> list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();//获取键名

        int columnCount = md.getColumnCount();//获取行的数量
        while (rs.next()) {
            Map rowData = new HashMap();//声明Map
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));//获取键名及值
            }
            list.add(rowData);
        }
        return list;
    }



}
