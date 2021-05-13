package jndc_client.core;


import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCConfigCenter;
import jndc.core.NDCMessageProtocol;
import jndc.utils.InetUtils;
import jndc.utils.UniqueInetTagProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * client config center
 */
public class JNDCClientConfigCenter implements NDCConfigCenter {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    private JNDCClientMessageHandle currentHandler;

    //store
    private Map<String, ClientServiceProvider> portProtectorMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext channelHandlerContext;//A client  holds only one tunnel


    private volatile AtomicBoolean connectionToServerState =new AtomicBoolean(false);

    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
        channelHandlerContext.writeAndFlush(ndcMessageProtocol);
    }

    @Override
    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {


        int localPort = ndcMessageProtocol.getLocalPort();
        InetAddress localInetAddress = ndcMessageProtocol.getLocalInetAddress();

        //service provider ip address+port
        String client = UniqueInetTagProducer.get4Client(localInetAddress, localPort);


        ClientServiceProvider clientServiceProvider = portProtectorMap.get(client);

        if (clientServiceProvider == null) {
            throw new RuntimeException("cant fount the service");
        }

        //receive message
        clientServiceProvider.receiveMessage(ndcMessageProtocol);
    }




    public void registerMessageChannel(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }


    /**
     * be called when "the face tcp"  is interrupted,
     * we need to interrupt local application at the same time
     *
     * @param ndcMessageProtocol
     */
    public void shutDownClientServiceProvider(NDCMessageProtocol ndcMessageProtocol) {
        int localPort = ndcMessageProtocol.getLocalPort();
        InetAddress localInetAddress = ndcMessageProtocol.getLocalInetAddress();
        String client = UniqueInetTagProducer.get4Client(localInetAddress, localPort);
        ClientServiceProvider clientServiceProvider = portProtectorMap.get(client);
        if (clientServiceProvider == null) {
            logger.error("can not find the service provider of "+client);
            return;
        }

        int remotePort = ndcMessageProtocol.getRemotePort();
        InetAddress remoteInetAddress = ndcMessageProtocol.getRemoteInetAddress();
        String client1 = UniqueInetTagProducer.get4Client(remoteInetAddress, remotePort);
        clientServiceProvider.releaseRelatedResources(client1);
    }

    /**
     * register service the manage map
     * @param x
     */
    public void initService(ClientServiceDescription x) {
        ClientServiceProvider clientServiceProvider = new ClientServiceProvider(x.getServicePort(), x.getServiceIp());

        InetAddress byStringIpAddress = InetUtils.getByStringIpAddress(x.getServiceIp());

        //service provider ip address + port
        String clientTag = UniqueInetTagProducer.get4Client(byStringIpAddress, x.getServicePort());

        ClientServiceProvider clientServiceProvider1 = portProtectorMap.get(clientTag);
        if (clientServiceProvider1!=null){
            logger.error("the service "+clientTag+" has been register in this map");
            return;
        }

        portProtectorMap.put(clientTag, clientServiceProvider);
    }

    public void destroyService(ClientServiceDescription x) {
        InetAddress byStringIpAddress = InetUtils.getByStringIpAddress(x.getServiceIp());
        String clientTag = UniqueInetTagProducer.get4Client(byStringIpAddress, x.getServicePort());
        ClientServiceProvider remove = portProtectorMap.remove(clientTag);
        if (remove==null){
            logger.error("can not found service "+clientTag+" in local");
            return;
        }else {
            remove.releaseAllRelatedResources();
        }
    }




    public JNDCClientMessageHandle getCurrentHandler() {
        return currentHandler;
    }

    public void setCurrentHandler(JNDCClientMessageHandle currentHandler) {
        this.currentHandler = currentHandler;
    }

    public void successToConnectToServer() {
        connectionToServerState.set(true);
    }

    public void failToConnectToServer() {
        connectionToServerState.set(false);
    }

    public boolean getCurrentClientConnectionState(){
        return connectionToServerState.get();
    }
}
