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

/**
 * client config center
 */
public class JNDCClientConfigCenter implements NDCConfigCenter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, ClientServiceProvider> portProtectorMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext channelHandlerContext;//A client temporarily holds only one tunnel


    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
        channelHandlerContext.writeAndFlush(ndcMessageProtocol);
    }

    @Override
    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {
        int localPort = ndcMessageProtocol.getLocalPort();
        InetAddress localInetAddress = ndcMessageProtocol.getLocalInetAddress();
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
        }

        int remotePort = ndcMessageProtocol.getRemotePort();
        InetAddress remoteInetAddress = ndcMessageProtocol.getRemoteInetAddress();
        String client1 = UniqueInetTagProducer.get4Client(remoteInetAddress, remotePort);
        clientServiceProvider.releaseRelatedResources(client1);
    }

    public void initService(ClientServiceDescription x) {
        InetAddress byStringIpAddress = InetUtils.getByStringIpAddress(x.getServiceIp());
        String client = UniqueInetTagProducer.get4Client(byStringIpAddress, x.getServicePort());


        ClientServiceProvider clientServiceProvider = new ClientServiceProvider(x.getServicePort(), x.getServiceIp());
        portProtectorMap.put(client, clientServiceProvider);
    }
}
