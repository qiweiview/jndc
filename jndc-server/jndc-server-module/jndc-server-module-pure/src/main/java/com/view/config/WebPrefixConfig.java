package com.view.config;

import com.view.annotation.AdminPrefix;
import com.view.constant.WebPrefixConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-04-29 20:51
 * @description: 不同资源路径前缀配置
 */
@Configuration
public class WebPrefixConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 后台
        configurer.addPathPrefix(WebPrefixConstants.ADMIN, c -> c.isAnnotationPresent(AdminPrefix.class));
    }
}
