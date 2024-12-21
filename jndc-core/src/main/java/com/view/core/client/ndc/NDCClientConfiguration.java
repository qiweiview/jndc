package com.view.core.client.ndc;

import com.view.core.model.CheckAbleConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Data
@Slf4j
public class NDCClientConfiguration extends CheckAbleConfiguration {
    //IP
    private String host;

    //端口
    private int port;

    //唯一ID
    private String uniqueId;

    //重连次数
    private int reconnectTimes = 0;

    //重连次数限制
    private int reconnectLimit = 0;

    //是否自动重连
    private Boolean autoReconnect;

    //超时时间
    private int timeoutSecond = 15;

    //启动回调
    private Runnable startedCallback;

    //停止回调
    private Runnable stopCallback;

    //失败回调
    private Consumer<Exception> failCallback;

    public void printConfiguration() {
        log.info("启动客户端使用配置：IP:{},端口:{},超时:{}秒", host, port, timeoutSecond);
    }

    public boolean reconnectThisTime() {
        if (autoReconnect) {
            if (reconnectLimit == -1 || reconnectTimes < reconnectLimit) {
                reconnectTimes++;
                return true;
            }
        }
        return false;
    }

    @Override
    public void check() {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("host不能为空");
        }
        if (port <= 0) {
            throw new IllegalArgumentException("port必须大于0");
        }

        if (uniqueId == null || uniqueId.isEmpty()) {
            throw new IllegalArgumentException("uniqueId不能为空");
        }

        if (startedCallback == null) {
            throw new IllegalArgumentException("startedCallback不能为空");
        }

        if (failCallback == null) {
            throw new IllegalArgumentException("failCallback不能为空");
        }

        if (timeoutSecond <= 0) {
            throw new IllegalArgumentException("timeoutSecond必须大于0");
        }

        if (reconnectLimit <= 0 && reconnectLimit != -1) {
            throw new IllegalArgumentException("reconnectLimit必须大于0");
        }

        if (autoReconnect == null) {
            throw new IllegalArgumentException("autoReconnect不能为空");
        }
    }
}
