package jndc.core.data_store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@DSTable(name = "sqlite_master")
public class SQLiteTableSupport {

    private String tbl_name;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String sql;

    public SQLiteTableSupport() {
    }

    public SQLiteTableSupport(String tbl_name, String sql) {
        this.tbl_name = tbl_name;
        this.sql = sql;
    }

    public String getTbl_name() {
        return tbl_name;
    }

    public void setTbl_name(String tbl_name) {
        this.tbl_name = tbl_name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
