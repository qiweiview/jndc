package jndc_server.core;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCConfigCenter;
import jndc.core.NDCMessageProtocol;

import jndc.utils.InetUtils;
import jndc_server.databases_object.ChannelContextCloseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * server config center ,heart of this app
 */
public class NDCServerConfigCenter implements NDCConfigCenter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, ChannelHandlerContextHolder> channelHandlerContextHolderMap = new ConcurrentHashMap<>();

    //port service bind,there is a 'ServerPortProtector' in every  'ServerPortBindContext'
    private Map<Integer, ServerPortBindContext> tcpRouter = new ConcurrentHashMap<>();


    public void removeServiceByChannelId(String channelId,List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers){
        if (channelId==null){
            throw new RuntimeException( "channelId is necessary");
        }

        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(channelId);
        if (channelHandlerContextHolder==null){
            logger.error("can not found the holder that id is"+channelId);
        }else {
            channelHandlerContextHolder.removeTcpServiceDescriptions(tcpServiceDescriptionOnServers);
        }
    }

    public void addServiceByChannelId(String channelId,List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers){
        if (channelId==null){
           throw new RuntimeException( "channelId is necessary");
        }
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(channelId);
        if (channelHandlerContextHolder==null){
            logger.error("can not found the holder that id is"+channelId);
        }else {
            channelHandlerContextHolder.addTcpServiceDescriptions(tcpServiceDescriptionOnServers);
        }

    }

    public void registerServiceProvider(ChannelHandlerContextHolder channelHandlerContextHolder) {
        logger.debug(channelHandlerContextHolder.getContextIp() + " register " + channelHandlerContextHolder.serviceNum() + " service");
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


    /**
     * bind local service to a server port
     * @param port
     * @param y
     * @return
     */
    public boolean addTCPRouter(int port, TcpServiceDescriptionOnServer y) {
       if (tcpRouter.get(port)!=null){
           //todo exist a running context
           logger.error("exist a context bind the port: "+port);
          return true;//return true to change the state to success
       }


        //create bind context
        ServerPortBindContext serverPortBindContext = new ServerPortBindContext(port);
        serverPortBindContext.setTcpServiceDescription(y);

        //openTCPPortListener
        ServerPortProtector serverPortProtector = new ServerPortProtector(serverPortBindContext.getPort());
        boolean success = serverPortProtector.start();//this step is asynchronous
        if (!success) {//check the port has been monitored or not
            //do rollback
            serverPortBindContext.setTcpServiceDescription(null);
            serverPortProtector.releaseRelatedResources();
            return false;
        }

        serverPortBindContext.setServerPortProtector(serverPortProtector);//set serverPortProtector to the context

        //add into  service release list in TcpServiceDescription
        y.addToServiceReleaseList(serverPortProtector);

        //register the context
        tcpRouter.put(port, serverPortBindContext);
        return true;

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

    public void refreshHeartBeatTimeStamp(String channelId) {
        ChannelHandlerContextHolder channelHandlerContextHolder = channelHandlerContextHolderMap.get(channelId);
        if (channelHandlerContextHolder!=null){
            channelHandlerContextHolder.refreshHeartBeatTimeStamp();
        }

    }

    public void checkChannelHealthy() {
        channelHandlerContextHolderMap.forEach((k,v)->{
            if (v.checkUnreachable()){
                v.getChannelHandlerContext().close();
            }
        });
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
        TcpServiceDescriptionOnServer tcpServiceDescription = serverPortBindContext.getTcpServiceDescription();
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
