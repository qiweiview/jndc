package jndc.client;


import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCConfigCenter;
import jndc.core.NDCMessageProtocol;
import jndc.core.PortProtector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * client config center
 */
public class JNDCClientConfigCenter implements NDCConfigCenter {

    //use port as key because the message from server  is not just sent to a port
    private Map<Integer, ClientPortProtector> portProtectorMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext channelHandlerContext;//A client temporarily holds only one tunnel


    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
        channelHandlerContext.writeAndFlush(ndcMessageProtocol);
    }

    @Override
    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {

        int localPort = ndcMessageProtocol.getLocalPort();
        ClientPortProtector clientPortProtector = portProtectorMap.get(localPort);
        if (clientPortProtector == null) {
            //create port protector
            clientPortProtector = new ClientPortProtector(localPort);
            //register port protector
            registerPortProtector(localPort, clientPortProtector);
        }

        //receive message
        clientPortProtector.receiveMessage(ndcMessageProtocol);


    }


    public void registerMessageChannel(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }



    public void registerPortProtector(int port, PortProtector portProtector) {
        ClientPortProtector clientPortProtector = (ClientPortProtector) portProtector;
        portProtectorMap.put(port, clientPortProtector);
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
