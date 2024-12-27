package com.view.core.client.tcp;

import com.view.core.model.CheckAbleConfiguration;
import com.view.core.model.TCPDataTransport;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
public class TCPClientConfiguration extends CheckAbleConfiguration {

    private String host;

    private int port;

    private Consumer<TCPClient> startSuccessCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPClient.class);

    private Consumer<TCPClient> startFailCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPClient.class);

    private Consumer<ChannelHandlerContext> activeCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(ChannelHandlerContext.class);

    private Consumer<TCPDataTransport> readCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPDataTransport.class);

    private BiConsumer<TCPDataTransport, TCPClient> readCompleteCallBack = CheckAbleConfiguration.EMPTY_BICONSUMER(TCPDataTransport.class, TCPClient.class);

    private Consumer<TCPDataTransport> inactiveCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPDataTransport.class);

    @Override
    public void check() {
        if (host == null) {
            throw new RuntimeException("host is null");
        }

        if (port <= 0) {
            throw new RuntimeException("port is invalid");
        }
    }
}
