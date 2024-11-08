package com.view.jndc.server.config.dynamic_datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源
 *
 * @author zy
 * @date 2020-05-20
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    public final static String DB_READ = "DB_DATA";
    public final static String DB_WRITE = "DB_WRITE";
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /**
     * 配置DataSource, defaultTargetDataSource为主数据库
     */
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceKey = getDataSourceKey();
        Thread currentThread = Thread.currentThread();
        log.debug(currentThread + "线程使用：" + dataSourceKey);

        return dataSourceKey;
    }

    public static void setDataSourceKey(String dataSource) {
        Thread currentThread = Thread.currentThread();
        log.debug(currentThread + "线程设置：" + dataSource);
        contextHolder.set(dataSource);
    }

    public static String getDataSourceKey() {
        return contextHolder.get();
    }

    public static void clearDataSourceKey() {
        contextHolder.remove();
    }

}