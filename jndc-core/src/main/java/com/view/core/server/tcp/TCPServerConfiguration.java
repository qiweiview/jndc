package com.view.core.server.tcp;


import com.view.core.model.CheckAbleConfiguration;
import com.view.core.model.TCPDataTransport;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


@Data
public class TCPServerConfiguration {
    private String serviceId;

    private String ndcClientId;

    private int port;

    //启动回调
    private Consumer<TCPServer> startSuccessCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPServer.class);

    //启动失败回调
    private Consumer<TCPServer> startFailCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPServer.class);

    //停止回调
    private Consumer<TCPServer> stopCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPServer.class);

    //连接启动回调
    private BiConsumer<TCPDataTransport, ChannelHandlerContext> activeCallBack = CheckAbleConfiguration.EMPTY_BICONSUMER(TCPDataTransport.class, ChannelHandlerContext.class);

    //读取数据回调
    private Consumer<TCPDataTransport> readCallBack = CheckAbleConfiguration.EMPTY_CONSUMER(TCPDataTransport.class);

    //读取完成回调
    private BiConsumer<TCPDataTransport, TCPServer> readCompleteCallBack = CheckAbleConfiguration.EMPTY_BICONSUMER(TCPDataTransport.class, TCPServer.class);

    //来凝结关闭回调
    private BiConsumer<TCPDataTransport, TCPServer> inactiveCallBack = CheckAbleConfiguration.EMPTY_BICONSUMER(TCPDataTransport.class, TCPServer.class);

}
