package com.view.core.model.heart_beat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum HeartBeatSource {
    SERVER("服务端", "SERVER"),

    CLIENT("客户端", "CLIENT"),

    ;

    public String label;

    public String value;

    HeartBeatSource(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
