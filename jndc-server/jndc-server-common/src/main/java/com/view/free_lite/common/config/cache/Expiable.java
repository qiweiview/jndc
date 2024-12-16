package com.view.free_lite.common.config.cache;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Expiable {
    private long expireTime;
    private String value;

    public Expiable(String value, long expireTime) {
        this.expireTime = expireTime;
        this.value = value;
    }

    /**
     * 是否过期
     * @return
     */
    public boolean expire() {
        long l = System.currentTimeMillis();
        log.info("当前：{}，过期：{}", l, expireTime);

        if (expireTime < 0) {
            return false;
        }
        return  l> expireTime;
    }
}
