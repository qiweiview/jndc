package jndc.server;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCConfigCenter;
import jndc.core.NDCMessageProtocol;
import jndc.core.PortProtector;
import jndc.core.ServerPortProtector;
import jndc.utils.LogPrint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * server config center ,heart of this app
 */
public class NDCServerConfigCenter implements NDCConfigCenter {

    private Map<Integer, ServerPortProtector> portProtectorMap = new ConcurrentHashMap<>();
    private ChannelHandlerContext channelHandlerContext;//single channel  ,expect a list  of channels


    public void startPortMonitoring(NDCMessageProtocol copy) {

        int serverPort = copy.getServerPort();

        if (portProtectorMap.containsKey(serverPort)) {
            LogPrint.log("port has been monitored");
            return;
        }

        //create port Protector and start
        ServerPortProtector serverPortProtector = new ServerPortProtector();
        serverPortProtector.start(copy, this);
    }

    /**
     * be called when ServerPortProtector created
     * a serverPortProtector hold multiple tcp connection
     *
     * @param port
     * @param portProtector
     */
    @Override
    public void registerPortProtector(int port, PortProtector portProtector) {
        ServerPortProtector serverPortProtector = (ServerPortProtector) portProtector;
        ServerPortProtector serverPortProtector1 = portProtectorMap.get(port);
        if (serverPortProtector1 != null) {
            //impossible to this ,but just in case
            //in the server one port just create one portProtector，more than one PortProtector are not allowed
            serverPortProtector1.shutDown();
        }
        portProtectorMap.put(port, serverPortProtector);
    }

    /**
     * be called when ServerPortProtector shutdown
     *
     * @param port
     */
    @Override
    public void unRegisterPortProtector(int port) {
        portProtectorMap.remove(port);
    }


    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
        channelHandlerContext.writeAndFlush(ndcMessageProtocol);
    }






    @Override
    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {

        int serverPort = ndcMessageProtocol.getServerPort();

        //需要解决
        ServerPortProtector serverPortProtector = portProtectorMap.get(serverPort);
        if (serverPortProtector == null) {
            //todo drop message
            return;
        } else {
            serverPortProtector.receiveMessage(ndcMessageProtocol);
        }

    }

    /**
     * called when a new channel is discovered
     *
     * @param channelHandlerContext
     */
    @Override
    public void registerMessageChannel(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
}
