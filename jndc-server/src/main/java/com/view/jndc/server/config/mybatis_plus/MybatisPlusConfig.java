package com.view.jndc.server.config.mybatis_plus;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    //读取yml配置文件中的配置active
    @Value("${spring.profiles.active}")
    private String active;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor;
        if ("h2".equalsIgnoreCase(active)) {
            paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.H2);
        } else {
            paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        }
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

}
