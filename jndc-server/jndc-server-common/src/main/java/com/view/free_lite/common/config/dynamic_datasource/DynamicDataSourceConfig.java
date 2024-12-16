package com.view.free_lite.common.config.dynamic_datasource;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置多数据源
 */
@Configuration
@Data
public class DynamicDataSourceConfig {


    private Map<Object, Object> dynamicDatasourceMap = new HashMap();

    private DynamicDataSource dynamicDataSource;

   /* @Bean
    @ConfigurationProperties("spring.datasource.druid.read")
    public DataSource readDataSource() {
        DataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }*/

    @Bean
    @ConfigurationProperties("spring.datasource.druid.write")
    public DataSource writedDataSource() {
        DataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }

    /**
     * 如果还有数据源,在这继续添加 DataSource Bean
     */


    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource() {
        //初始化两个
//        dynamicDatasourceMap.put(DynamicDataSource.DB_READ, readDataSource());
        dynamicDatasourceMap.put(DynamicDataSource.DB_WRITE, writedDataSource());
        dynamicDataSource = new DynamicDataSource(writedDataSource(), dynamicDatasourceMap);
        return dynamicDataSource;
    }


    /**
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static DruidDataSource createDruidDataSource(String url, String username, String password) {
        DruidDataSource dataSource = DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .type(DruidDataSource.class)
                .build();
        dataSource.setInitialSize(1);
        return dataSource;
    }


}
