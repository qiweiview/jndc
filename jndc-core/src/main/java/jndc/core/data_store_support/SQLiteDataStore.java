package jndc.core.data_store_support;


import jndc.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    public void flywayInit() {
        String p1 = System.getProperty("user.dir") + File.separator + ".." + File.separator + "conf" + File.separator + "db" + File.separator + "migration_sqlite";
        String p2 = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "conf" + File.separator + "db" + File.separator + "migration_sqlite";
        String devPath = "filesystem:" + PathUtils.findExistPath(p1, p2);
        log.info("flyway 读取路径：" + devPath);
//        Flyway flyway = Flyway.configure()
//                .locations("classpath:db/migration_sqlite", devPath)
//                .baselineOnMigrate(true)
//                .dataSource(url, "", "")
//                .load();
//        flyway.migrate();
    }
}
