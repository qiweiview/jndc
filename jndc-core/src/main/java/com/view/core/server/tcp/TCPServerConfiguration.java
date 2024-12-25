package com.view.core.server.tcp;


import com.view.core.model.CheckAbleConfiguration;
import com.view.core.model.TCPDataTransport;
import lombok.Data;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


@Data
public class TCPServerConfiguration {

    private Consumer<TCPServer> startSuccessCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPServer.class);

    private Consumer<TCPServer> startFailCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPServer.class);

    private BiConsumer<TCPDataTransport, TCPServer> activeCallBack = CheckAbleConfiguration.EMPTY_BICONSUMER(TCPDataTransport.class, TCPServer.class);

    private Consumer<TCPDataTransport> readCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPDataTransport.class);

    private BiConsumer<TCPDataTransport, TCPServer> readCompleteCallBack = CheckAbleConfiguration.EMPTY_BICONSUMER(TCPDataTransport.class, TCPServer.class);

    private BiConsumer<TCPDataTransport, TCPServer> inactiveCallBack = CheckAbleConfiguration.EMPTY_BICONSUMER(TCPDataTransport.class, TCPServer.class);

}
