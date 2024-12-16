package com.view.free_lite.common.config.cache;


import com.view.free_lite.common.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JMemoryCache {
    private Map<String, Expiable> cache = new ConcurrentHashMap<>();



    public <T> T get(String key, Class<T> tClass) {
        Expiable expiable = cache.get(key);
        if (expiable == null) {
            return null;
        }

        //判断过期
        if (expiable.expire()) {
           return null;
        }
        return Jackson.toObject(expiable.getValue(), tClass);
    }

    public boolean delete(String key) {
        return cache.remove(key) != null;
    }

    public long delete(Collection collection) {
        collection.forEach(cache::remove);
        return collection.size();
    }

    public Collection<String> keys(String pattern) {
        List<String> collect = cache
                .keySet()
                .stream()
                .filter(key -> key.matches(pattern))
                .collect(Collectors.toList());
        return collect;
    }


    public <T> void set(String key, T value, Integer timeout, TimeUnit timeUnit) {
        if (key == null || value == null) {
            throw new RuntimeException("key和value不能为空");
        }

        long expireTime = -1l;
        if (timeout != null && timeUnit != null && timeout > 0) {
            {
                expireTime = System.currentTimeMillis() + timeUnit.toSeconds(timeout) * 1000;
            }
        }

        String json = Jackson.toJson(value);
        Expiable expiable = new Expiable(json, expireTime);

        cache.put(key.toString(), expiable);
    }

    public <T> void set(String key, T value) {
        set(key, value, null, null);
    }



}
