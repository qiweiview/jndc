package jndc.server;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.*;
import jndc.utils.LogPrint;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * server config center ,heart of this app
 */
public class NDCServerConfigCenter implements NDCConfigCenter {

    private Map<Integer, ServerPortProtector> portProtectorMap = new ConcurrentHashMap<>();
    private Map<Integer, ChannelHandlerContextHolder> contextHolderMap = new ConcurrentHashMap<>();//a client use one tcp connection

    //  private ChannelHandlerContext channelHandlerContext;//single channel  ,expect a list  of channels


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
        ChannelHandlerContextHolder channelHandlerContextHolder = contextHolderMap.get(port);
        if (channelHandlerContextHolder == null) {
            //no holder interrupt
            return;
        }


        ServerPortProtector serverPortProtector = (ServerPortProtector) portProtector;
        ServerPortProtector serverPortProtector1 = portProtectorMap.get(port);
        if (serverPortProtector1 != null) {
            //impossible to this ,but just in case
            //in the server one port just create one portProtector，more than one PortProtector are not allowed
            serverPortProtector1.shutDown();
        }
        portProtectorMap.put(port, serverPortProtector);//this map maybe store different serverPortProtector from different client
        channelHandlerContextHolder.addServerPortProtector(port, serverPortProtector);//this list just store one client serverPortProtector list
    }

    /**
     * be called when ServerPortProtector shutdown
     * only remove the record ,not do the terminating operation
     *
     * @param port
     */
    @Override
    public void unRegisterPortProtector(int port) {
        portProtectorMap.remove(port);
    }


    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
        int serverPort = ndcMessageProtocol.getServerPort();
        ChannelHandlerContextHolder channelHandlerContextHolder = contextHolderMap.get(serverPort);
        if (channelHandlerContextHolder == null) {
            //drop message
            return;
        }
        ChannelHandlerContext channelHandlerContext = channelHandlerContextHolder.getChannelHandlerContext();
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
    public void registerMessageChannel(int port, ChannelHandlerContext channelHandlerContext) {
        ChannelHandlerContextHolder channelHandlerContextHolder = contextHolderMap.get(port);
        if (channelHandlerContextHolder != null) {
            throw new RuntimeException("same port has been register");
        }
        ChannelHandlerContextHolder channelHandlerContextHolder1 = new ChannelHandlerContextHolder(channelHandlerContext);
        contextHolderMap.put(port, channelHandlerContextHolder1);
    }

    @Override
    public void unRegisterMessageChannel(ChannelHandlerContext channelHandlerContext) {
        contextHolderMap.forEach((k, v) -> {
            ChannelHandlerContext store = v.getChannelHandlerContext();
            if (store == channelHandlerContext) {
                List<Integer> serverPorts = v.getServerPorts();
                serverPorts.forEach(x -> {
                    unRegisterPortProtector(x);
                });
                v.shutDownServerPortProtectors();//shut down all
                contextHolderMap.remove(k);
            }
        });
        LogPrint.log("now channelHandlerContext Map:" + contextHolderMap);
    }


    /**
     * be called when the local application is interrupted,we need to interrupt "the face tcp" at the same time
     * @param ndcMessageProtocol
     */
    public void shutDownTcpConnection(NDCMessageProtocol ndcMessageProtocol) {
        int serverPort = ndcMessageProtocol.getServerPort();

        //需要解决
        ServerPortProtector serverPortProtector = portProtectorMap.get(serverPort);
        if (serverPortProtector == null) {
            //do nothing
            return;
        } else {
            serverPortProtector.shutDownTcpConnection(ndcMessageProtocol);
        }

    }


}
