package com.view.core.server.ndc;

import com.view.core.model.CheckAbleConfiguration;
import lombok.Data;

import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class NDCServerConfiguration extends CheckAbleConfiguration {
    private String uniqueId;

    private String host;

    private int port;

    /*------服务端本身------*/
    private Runnable startedCallback = EMPTY_CALLBACK;

    private Runnable stopCallback = EMPTY_CALLBACK;

    private Consumer<Exception> failCallback = EMPTY_FAIL_CALLBACK;

    /*------服务端连接------*/
    private Function<SessionContext, SessionContext> connectActiveCallback = EMPTY_FUNCTION(SessionContext.class);

    private Consumer<SessionContext> openChannelCallback = EMPTY_CONSUMER(SessionContext.class);

    private Consumer<SessionContext> connectInActiveCallback = EMPTY_CONSUMER(SessionContext.class);


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


    }
}
