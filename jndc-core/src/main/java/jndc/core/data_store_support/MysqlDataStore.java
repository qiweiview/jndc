package jndc.core.data_store_support;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * mysql
 */
@Slf4j
public class MysqlDataStore extends DataStoreAbstract {
    private String url;

    private String name;

    private String password;

    private volatile Connection connection;

    public MysqlDataStore(String url, String name, String password) {
        this.url = url;
        this.name = name;
        this.password = password;
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                synchronized (this) {
                    if (connection == null || connection.isClosed()) {
                        connection = DriverManager.getConnection(url, name, password);
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
        String devPath = "filesystem:" + System.getProperty("user.dir") + File.separator + "jndc_server\\src\\main\\resources\\db\\migration_mysql";
//        Flyway flyway = Flyway.configure()
//                .locations("classpath:db/migration_mysql", devPath)
//                .baselineOnMigrate(true)
//                .dataSource(url, name, password)
//                .load();
//
//        flyway.migrate();
    }
}
