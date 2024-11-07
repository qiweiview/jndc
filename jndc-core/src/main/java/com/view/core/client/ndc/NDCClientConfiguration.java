package com.view.core.client.ndc;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class NDCClientConfiguration {
    private String host;

    private int port;

    private int timeoutSecond = 15;

    public void printConfiguration() {
        log.info("启动客户端使用配置：IP:{},端口:{},超时:{}秒", host, port, timeoutSecond);
    }
}
