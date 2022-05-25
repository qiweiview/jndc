package jndc_server.config;

import lombok.Data;

@Data
public class DBConfig {

    private static final String TYPE_MYSQL = "mysql";

    private static final String TYPE_SQLITE = "sqlite";

    /**
     * 类型
     * mysql
     * sqlite
     */
    private String type;

    /**
     *
     */
    private String url;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String password;

    private boolean flywayEnable;

    public boolean useMysql() {
        String type = getType();
        if (type == null) {
            return false;
        }

        return TYPE_MYSQL.equals(type.toLowerCase());
    }
}
