package jndc.core;

import io.netty.channel.ChannelHandlerContext;

public interface NDCConfigCenter {

    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol);

    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol);

    public void registerMessageChannel(int port,ChannelHandlerContext channelHandlerContext);

    void unRegisterMessageChannel(ChannelHandlerContext channelHandlerContext);

    public void registerPortProtector(int port, PortProtector portProtector);

    public void unRegisterPortProtector(int port);



}
