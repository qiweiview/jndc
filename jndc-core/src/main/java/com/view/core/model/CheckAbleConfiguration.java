package com.view.core.model;


import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public abstract class CheckAbleConfiguration {
    public static <T> Function<T, T> EMPTY_FUNCTION(Class<T> tClass) {
        return (t) -> null;
    }

    public static final Runnable EMPTY_CALLBACK = () -> {
    };

    public static <T> Consumer<T> EMPTY_CONSUMER(Class<T> tClass) {
        return (t) -> {
        };
    }

    public static final Consumer<Exception> EMPTY_FAIL_CALLBACK = (e) -> {
        log.error("启动失败", e);
    };
    public abstract void check();
}
