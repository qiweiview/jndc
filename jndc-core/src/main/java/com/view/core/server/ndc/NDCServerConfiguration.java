package com.view.core.server.ndc;

import com.view.core.model.CheckAbleConfiguration;
import lombok.Data;

import java.util.function.Consumer;

@Data
public class NDCServerConfiguration extends CheckAbleConfiguration {
    private String uniqueId;

    private String host;

    private int port;

    private Runnable startedCallback;

    private Runnable stopCallback;

    private Consumer<Exception> failCallback;

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

        if (stopCallback == null) {
            throw new IllegalArgumentException("stopCallback不能为空");
        }

        if (failCallback == null) {
            throw new IllegalArgumentException("failCallback不能为空");
        }
    }
}
