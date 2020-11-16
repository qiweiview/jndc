package jndc.server;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.*;

import jndc.utils.InetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * server config center ,heart of this app
 */
public class NDCServerConfigCenter implements NDCConfigCenter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<ChannelHandlerContextHolder> channelHandlerContextHolders = new CopyOnWriteArrayList<>();//channel list

    private Map<Integer, ServerPortBindContext> tcpRouter = new ConcurrentHashMap<>();

    public void registerServiceProvider(ChannelHandlerContextHolder channelHandlerContextHolder) {
        logger.info(channelHandlerContextHolder.getContextIp() + " register " + channelHandlerContextHolder.serviceNum() + " service");
        channelHandlerContextHolders.add(channelHandlerContextHolder);

    }

    public void unRegisterServiceProvider(ChannelHandlerContext inactive) {
        InnerCondition<ChannelHandlerContext> innerCondition = (x, y) -> x.contextBelong(y);
        unRegisterServiceProvider(inactive, innerCondition);
    }

    public void unRegisterServiceProvider(String del) {
        InnerCondition<String> innerCondition = (x, y) -> x.sameId(y);
        unRegisterServiceProvider(del, innerCondition);
    }


    private void unRegisterServiceProvider(Object object, InnerCondition innerCondition) {
        for (int i = 0; i < channelHandlerContextHolders.size(); i++) {
            ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolders.get(i);
            if (innerCondition.check(channelHandlerContextHolder, object)) {// check is the holder list

               //do something about resource release
                channelHandlerContextHolder.releaseRelatedResources();
                channelHandlerContextHolders.remove(i);
                break;
            }
        }
    }

    public void addTCPRouter(int port, TcpServiceDescription y) {
        //create bind context
        ServerPortBindContext serverPortBindContext = new ServerPortBindContext(port);
        serverPortBindContext.setTcpServiceDescription(y);

        //openTCPPortListener
        ServerPortProtector serverPortProtector = new ServerPortProtector(serverPortBindContext.getPort());
        serverPortBindContext.setServerPortProtector(serverPortProtector);
        serverPortProtector.start();
        y.addToServiceReleaseList(serverPortProtector);


        tcpRouter.put(port, serverPortBindContext);

    }

    /**
     * interrupt  because accept the signal from service provider
     * @param ndcMessageProtocol
     */
    public void connectionInterrupt(NDCMessageProtocol ndcMessageProtocol) {
        int serverPort = ndcMessageProtocol.getServerPort();
        ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
        if (serverPortBindContext==null){
            //todo drop
            logger.error("can not found the ServerPortBindContext for port :"+serverPort);
        }else {
            serverPortBindContext.connectionInterrupt(ndcMessageProtocol);
        }


    }


    /**
     * inner
     *
     * @param <T>
     */
    private interface InnerCondition<T> {
        public boolean check(ChannelHandlerContextHolder channelHandlerContextHolder, T t);
    }


    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
        int serverPort = ndcMessageProtocol.getServerPort();
        ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
        if (serverPortBindContext == null) {
            //todo drop
            logger.info("can not found bind service for port:" + serverPort);
            return;
        }
        TcpServiceDescription tcpServiceDescription = serverPortBindContext.getTcpServiceDescription();
        tcpServiceDescription.sendMessage(ndcMessageProtocol);
    }

    @Override
    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {
        int serverPort = ndcMessageProtocol.getServerPort();
        ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
        if (serverPortBindContext == null) {
            //todo drop message
            logger.info("drop the message cause the port" + serverPort + "has not be listened");
        } else {
            serverPortBindContext.receiveMessage(ndcMessageProtocol);
        }
    }

    /* ------------------getter setter------------------ */

    public List<ChannelHandlerContextHolder> getChannelHandlerContextHolders() {
        return channelHandlerContextHolders;
    }

    public Map<Integer, ServerPortBindContext> getTcpRouter() {
        return tcpRouter;
    }
}
