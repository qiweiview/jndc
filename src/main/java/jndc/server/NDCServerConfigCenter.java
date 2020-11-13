package jndc.server;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;


/**
 * server config center ,heart of this app
 */
public class NDCServerConfigCenter implements NDCConfigCenter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<ChannelHandlerContextHolder> channelHandlerContextHolders = new CopyOnWriteArrayList<>();

    public void registerServiceProvider(ChannelHandlerContextHolder channelHandlerContextHolder) {
        logger.info(channelHandlerContextHolder.getContextIp() + " register " + channelHandlerContextHolder.serviceNum() + " service");
        channelHandlerContextHolders.add(channelHandlerContextHolder);

    }

    public void unRegisterServiceProvider(ChannelHandlerContext inactive) {
        InnerCondition<ChannelHandlerContext> innerCondition =(x, y)->x.contextBelong(y);
        unRegisterServiceProvider(inactive, innerCondition);
    }

    public void unRegisterServiceProvider(String del) {
        InnerCondition<String> innerCondition =(x, y)->x.sameId(y);
        unRegisterServiceProvider(del, innerCondition);
    }


    private void unRegisterServiceProvider(Object object, InnerCondition innerCondition) {
        for (int i = 0; i < channelHandlerContextHolders.size(); i++) {
            ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolders.get(i);
            if (innerCondition.check(channelHandlerContextHolder,object)) {// check is the holder list
                channelHandlerContextHolder.releaseRelatedResources();
                channelHandlerContextHolders.remove(i);
                break;
            }
        }
    }

    /**
     * inner
     * @param <T>
     */
    private interface InnerCondition<T>{
        public boolean check(ChannelHandlerContextHolder channelHandlerContextHolder,T t);
    }


    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {

    }

    @Override
    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {

    }

    public List<ChannelHandlerContextHolder> getChannelHandlerContextHolders() {
        return channelHandlerContextHolders;
    }


    //   // private Map<Integer, ServerPortProtector> portProtectorMap = new ConcurrentHashMap<>();
//    private Map<Integer, ChannelHandlerContextHolder> contextHolderMap = new ConcurrentHashMap<>();//a client use one tcp connection
//
//    //  private ChannelHandlerContext channelHandlerContext;//single channel  ,expect a list  of channels
//
//
//    public void startPortMonitoring(NDCMessageProtocol copy) {
//
////        int serverPort = copy.getServerPort();
////
////        if (contextHolderMap.containsKey(serverPort)) {
////            logger.info("port has been monitored");
////            return;
////        }
//
//        //create port Protector and start
////        ServerPortProtector serverPortProtector = new ServerPortProtector();
////        serverPortProtector.start(copy, this);
//    }
//
//    /**
//     * be called when ServerPortProtector created
//     * a serverPortProtector hold multiple face tcp connection
//     *
//     * @param port
//     * @param serverPortProtector
//     */
//    public void registerPortProtector(int port, ServerPortProtector serverPortProtector) {
//        ChannelHandlerContextHolder channelHandlerContextHolder = contextHolderMap.get(port);
//        if (channelHandlerContextHolder == null) {
//            //no holder interrupt
//            return;
//        }
//
//
//        //ServerPortProtector serverPortProtector = (ServerPortProtector) portProtector;
////        ServerPortProtector serverPortProtector1 = portProtectorMap.get(port);
////        if (serverPortProtector1 != null) {
////            //impossible to this ,but just in case
////            //in the server one port just create one portProtector，more than one PortProtector are not allowed
////            serverPortProtector1.shutDownBeforeCreate();
////        }
//
//        //portProtectorMap.put(port, serverPortProtector);//this map maybe store different serverPortProtector from different client
//
//        channelHandlerContextHolder.addServerPortProtector(port, serverPortProtector);//this list just store one client serverPortProtector list
//    }
//
////    /**
////     * be called when ServerPortProtector shutdown
////     * only remove the record ,not do the terminating operation
////     *
////     * @param port
////     */
////    public void unRegisterPortProtector(int port) {
////        portProtectorMap.remove(port);
////    }
//
//
//    @Override
//    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
//        int serverPort = ndcMessageProtocol.getServerPort();
//        ChannelHandlerContextHolder channelHandlerContextHolder = contextHolderMap.get(serverPort);
//        if (channelHandlerContextHolder == null) {
//            //drop message
//            return;
//        }
//        ChannelHandlerContext channelHandlerContext = channelHandlerContextHolder.getChannelHandlerContext();
//        channelHandlerContext.writeAndFlush(ndcMessageProtocol);
//    }
//
//
//    @Override
//    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {
//
//        int serverPort = ndcMessageProtocol.getServerPort();
//
//        ChannelHandlerContextHolder channelHandlerContextHolder = contextHolderMap.get(serverPort);
//        channelHandlerContextHolder.getChannelHandlerContext()
//        //需要解决
//        ServerPortProtector serverPortProtector = contextHolderMap.get(serverPort);
//
//        if (serverPortProtector == null) {
//            //todo drop message
//            logger.debug("drop message with port" + serverPort);
//            return;
//        } else {
//            serverPortProtector.receiveMessage(ndcMessageProtocol);
//        }
//
//    }
//
//
//    /**
//     * called when a new channel is discovered
//     * @param port server face port
//     * @param channelHandlerContext
//     */
//    public void registerMessageChannel(int port, ChannelHandlerContext channelHandlerContext) {
//        ChannelHandlerContextHolder channelHandlerContextHolder = contextHolderMap.get(port);
//        if (channelHandlerContextHolder != null) {
//            throw new RuntimeException("same port has been register");
//        }
//        ChannelHandlerContextHolder channelHandlerContextHolder1 = new ChannelHandlerContextHolder(channelHandlerContext);
//        contextHolderMap.put(port, channelHandlerContextHolder1);
//    }
//
//    /**
//     * called when channel inactive
//     *
//     * @param channelHandlerContext
//     */
//    public void unRegisterMessageChannel(ChannelHandlerContext channelHandlerContext) {
//
//
//        Set<Map.Entry<Integer, ChannelHandlerContextHolder>> entries = contextHolderMap.entrySet();
//
//        Iterator<Map.Entry<Integer, ChannelHandlerContextHolder>> iterator = entries.iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Integer, ChannelHandlerContextHolder> next = iterator.next();
//            ChannelHandlerContextHolder value = next.getValue();
//            ChannelHandlerContext store = value.getChannelHandlerContext();
//
//            //find the ChannelHandlerContextHolder used inactive context
//            if (store == channelHandlerContext) {
//                //shutdown the  DownChannelHandlerContextHolder
//                shutDownChannelHandlerContextHolder(value);
//
//                //remove contextHolderMap record
//                iterator.remove();
//            }
//        }
//
//
//    }
//
//    /**
//     * shut down the ChannelHandlerContextHolder
//     * @param value
//     */
//    public void shutDownChannelHandlerContextHolder(ChannelHandlerContextHolder value){
//        ChannelHandlerContext channelHandlerContext = value.getChannelHandlerContext();
//        channelHandlerContext.close();
//
//
//        //get context relation server port
//        List<Integer> serverPorts = value.getServerPorts();
//
//        //just remove the record
//        serverPorts.forEach(x -> {
//            unRegisterPortProtector(x);
//        });
//
//        //shutdown ChannelHandlerContextHolder
//        value.shutDownServerPortProtectors();//shut down all
//    }
//
//    /**
//     * be called when the local application is interrupted,we need to interrupt "the face tcp" at the same time
//     *
//     * @param ndcMessageProtocol
//     */
//    public void shutDownTcpConnection(NDCMessageProtocol ndcMessageProtocol) {
//
//        int serverPort = ndcMessageProtocol.getServerPort();
//
//        ServerPortProtector serverPortProtector = portProtectorMap.get(serverPort);
//        if (serverPortProtector == null) {
//            //todo do nothing
//            return;
//        } else {
//            serverPortProtector.shutDownTcpConnection(ndcMessageProtocol);
//        }
//
//    }
//
//    public Map<Integer, ServerPortProtector> getPortProtectorMap() {
//        return portProtectorMap;
//    }
//
//    public Map<Integer, ChannelHandlerContextHolder> getContextHolderMap() {
//        return contextHolderMap;
//    }
}
