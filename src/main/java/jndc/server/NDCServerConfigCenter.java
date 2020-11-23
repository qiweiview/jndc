package jndc.server;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.*;

import jndc.utils.InetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetAddress;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * server config center ,heart of this app
 */
public class NDCServerConfigCenter implements NDCConfigCenter {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, ChannelHandlerContextHolder> channelHandlerContextHolderMap = new ConcurrentHashMap<>();
    private Map<Integer, ServerPortBindContext> tcpRouter = new ConcurrentHashMap<>();

    public void registerServiceProvider(ChannelHandlerContextHolder channelHandlerContextHolder) {
        logger.info(channelHandlerContextHolder.getContextIp() + " register " + channelHandlerContextHolder.serviceNum() + " service");
        channelHandlerContextHolderMap.put(channelHandlerContextHolder.getId(), channelHandlerContextHolder);

    }

    public ChannelContextCloseRecord unRegisterServiceProvider(ChannelHandlerContext inactive) {
        Set<Map.Entry<String, ChannelHandlerContextHolder>> entries = channelHandlerContextHolderMap.entrySet();
        Iterator<Map.Entry<String, ChannelHandlerContextHolder>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ChannelHandlerContextHolder> next = iterator.next();
            String key = next.getKey();
            ChannelHandlerContextHolder value = next.getValue();
            if (value.contextBelong(inactive)) {
                ChannelContextCloseRecord channelContextCloseRecord = ChannelContextCloseRecord.of(value);
                value.releaseRelatedResources();
                channelHandlerContextHolderMap.remove(key);
                return channelContextCloseRecord;
            }

        }
        logger.error("未匹配到对应隧道");
        return null;
    }

    public void sendHeartBeat(String id) {
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(id);
        if (channelHandlerContextHolder == null) {
            logger.error("未匹配到隧道:" + id);
            throw new RuntimeException("未匹配到隧道");

        } else {
            ChannelHandlerContext channelHandlerContext = channelHandlerContextHolder.getChannelHandlerContext();
            NDCMessageProtocol tqs = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.CHANNEL_HEART_BEAT);
            channelHandlerContext.writeAndFlush(tqs);
        }
    }


    public void unRegisterServiceProvider(String id) {
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(id);
        if (channelHandlerContextHolder == null) {
            logger.error("未匹配到隧道:" + id);
            throw new RuntimeException("未匹配到隧道");
        } else {
            channelHandlerContextHolder.getChannelHandlerContext().close();
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
     *
     * @param ndcMessageProtocol
     */
    public void connectionInterrupt(NDCMessageProtocol ndcMessageProtocol) {
        int serverPort = ndcMessageProtocol.getServerPort();
        ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPort);
        if (serverPortBindContext == null) {
            //todo drop
            logger.error("can not found the ServerPortBindContext for port :" + serverPort);
        } else {
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
        List<ChannelHandlerContextHolder> list = new ArrayList<>();
        Collection<ChannelHandlerContextHolder> values1 = channelHandlerContextHolderMap.values();
        values1.forEach(x -> {
            list.add(x);
        });

        return list;
    }

    public Map<Integer, ServerPortBindContext> getTcpRouter() {
        return tcpRouter;
    }
}
