package jndc.client;


import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCConfigCenter;
import jndc.core.NDCMessageProtocol;
import jndc.core.PortProtector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * server config center
 */
public class JNDCClientConfigCenter implements NDCConfigCenter {

    //use port as key because the message from server  is not just sent to a port
    private Map<Integer, ClientPortProtector> portProtectorMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext channelHandlerContext;//single channel  ,expect a list  of channels


    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
        channelHandlerContext.writeAndFlush(ndcMessageProtocol);
    }

    @Override
    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {

        int localPort = ndcMessageProtocol.getLocalPort();
        ClientPortProtector clientPortProtector = portProtectorMap.get(localPort);
        if (clientPortProtector == null) {
            clientPortProtector = new ClientPortProtector(localPort);

            //register port protector
            registerPortProtector(localPort, clientPortProtector);
        }

        clientPortProtector.receiveMessage(ndcMessageProtocol);


    }

    @Override
    public void registerMessageChannel(int port, ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    @Override
    public void unRegisterMessageChannel(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = null;
    }


    @Override
    public void registerPortProtector(int port, PortProtector portProtector) {
        ClientPortProtector clientPortProtector = (ClientPortProtector) portProtector;
        portProtectorMap.put(port, clientPortProtector);
    }

    @Override
    public void unRegisterPortProtector(int port) {

    }

    /**
     *  be called when "the face tcp"  is interrupted,we need to interrupt local application at the same time
     * @param ndcMessageProtocol
     */
    public void shutDownClientPortProtector(NDCMessageProtocol ndcMessageProtocol) {
        int localPort = ndcMessageProtocol.getLocalPort();
        ClientPortProtector clientPortProtector = portProtectorMap.get(localPort);
        if (clientPortProtector == null) {
            //do nothing
        }
        clientPortProtector.shutDown(ndcMessageProtocol);
    }
}
