package com.view.core.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * 数据插槽
 */
@Slf4j
@Data
public class DataSlot<T> {
    private Consumer<T> consumer;

    public DataSlot(Consumer<T> consumer) {
        this.consumer = consumer;
    }
}
