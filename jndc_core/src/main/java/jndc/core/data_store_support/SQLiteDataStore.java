package jndc.core.data_store_support;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * for sqlite only
 */
@Slf4j
public class SQLiteDataStore extends DataStoreAbstract {

    /**
     * 连接地址
     */
    private String url;

    private static final String SQL_LITE_DB = "jnc_db.db";

    private static final String PROTOCOL = "jdbc:sqlite:";

    /**
     * 存储目录
     */
    private String dbWorkDirect;

    private volatile Connection connection;

    public SQLiteDataStore(String dbWorkDirect) {
        this.dbWorkDirect = dbWorkDirect;
        if (!this.dbWorkDirect.endsWith(File.separator)) {
            this.dbWorkDirect += File.separator;
        }

        url = PROTOCOL + this.dbWorkDirect + SQL_LITE_DB;
        log.info("SQLite 工作目录:" + url);
    }


    /**
     * 获取连接
     *
     * @return
     */
    @Override
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                synchronized (this) {
                    if (connection == null || connection.isClosed()) {
                        connection = DriverManager.getConnection(url);
                    }
                }
            }
        } catch (SQLException sqlException) {
            log.error(sqlException + "");
        }

        return connection;
    }

    @Override
    public void initTables() {
        String resourcePath = "/db/init.sql";
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                log.error("未找到初始化脚本：" + resourcePath);
                return;
            }
            String sql;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                sql = sb.toString();
            }
            try (Statement stmt = getConnection().createStatement()) {
                for (String statement : sql.split(";")) {
                    String trimmed = statement.trim();
                    if ("".equals(trimmed)) {
                        continue;
                    }
                    stmt.execute(trimmed);
                }
            }
            log.info("数据库表初始化完成");
        } catch (Exception e) {
            log.error("数据库表初始化失败：" + e.getMessage(), e);
        }
    }
}
