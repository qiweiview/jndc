package com.view.runner;

import com.view.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-02 17:01
 * @description: 初始化系统配置至Redis中
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitConfigRunner implements CommandLineRunner {


    private final SysConfigService configService;

    @Override
    public void run(String... args) throws Exception {

        configService.resetConfigCache();
    }

}
