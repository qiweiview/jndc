package com.view.jndc.server.config.cache;

import com.view.jndc.server.config.exception.TokenExpireException;
import com.view.jndc.server.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class NDCMemoryCache {
    private Map<String, Expiable> cache = new ConcurrentHashMap<>();

    public void putExpired(Object key, Object value, Integer second) {
        if (key == null || value == null) {
            throw new RuntimeException("key和value不能为空");
        }

        long expireTime = -1l;
        if (second != null && second > 0) {
            {
                expireTime = System.currentTimeMillis() + second * 1000;
            }
        }

        String json = Jackson.toJson(value);
        Expiable expiable = new Expiable(json, expireTime);

        cache.put(key.toString(), expiable);
    }

    public void put(String key, Object value) {
        putExpired(key, value, -1);
    }

    public <T> T get(String key, Class<T> tClass) {
        Expiable expiable = cache.get(key);
        if (expiable == null) {
            return null;
        }

        if (expiable.expire()) {
            cache.remove(key);
            throw new TokenExpireException();
        }
        return Jackson.toObject(expiable.value, tClass);
    }


    private class Expiable {
        private long expireTime;
        private String value;

        public Expiable(String value, long expireTime) {
            this.expireTime = expireTime;
            this.value = value;
        }

        public boolean expire() {
            if (expireTime < 0) {
                return false;
            }
            return System.currentTimeMillis() > expireTime;
        }
    }
}
