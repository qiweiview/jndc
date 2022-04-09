package jndc_client.core;


import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCConfigCenter;
import jndc.core.NDCMessageProtocol;
import jndc.utils.InetUtils;
import jndc.utils.UniqueInetTagProducer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * client config center
 */
@Data
public class JNDCClientConfigCenter implements NDCConfigCenter {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    private JNDCClientMessageHandle currentHandler;

    //store
    private Map<String, ClientServiceProvider> portProtectorMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext channelHandlerContext;//A client  holds only one tunnel


    private volatile AtomicBoolean connectionToServerState = new AtomicBoolean(false);

    @Override
    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol) {
        channelHandlerContext.writeAndFlush(ndcMessageProtocol);
    }

    @Override
    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol) {


        int localPort = ndcMessageProtocol.getLocalPort();
        InetAddress localInetAddress = ndcMessageProtocol.getLocalAddress();

        //ip+端口 确定一个唯一的服务提供者标识
        String client = UniqueInetTagProducer.get4Client(localInetAddress, localPort);


        //哈希表路由
        ClientServiceProvider clientServiceProvider = portProtectorMap.get(client);

        if (clientServiceProvider == null) {
            logger.error("cant fount the service:" + client);
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
        InetAddress localInetAddress = ndcMessageProtocol.getLocalAddress();

        //唯一key: 本地客户端ip+本地客户端端口(唯一的一个服务提供者)
        String client = UniqueInetTagProducer.get4Client(localInetAddress, localPort);
        ClientServiceProvider clientServiceProvider = portProtectorMap.get(client);
        if (clientServiceProvider == null) {
            logger.error("无法获取服务提供者：" + client);
            return;
        }

        int remotePort = ndcMessageProtocol.getRemotePort();
        InetAddress remoteInetAddress = ndcMessageProtocol.getRemoteAddress();

        //唯一key: 远程客户端ip+远程客户端端口（唯一的一个访问者）
        String client1 = UniqueInetTagProducer.get4Client(remoteInetAddress, remotePort);

        //释放连接
        clientServiceProvider.releaseRelatedResources(client1);
    }

    /**
     * register service the manage map
     *
     * @param x
     */
    public void initService(ClientServiceDescription x) {


        InetAddress byStringIpAddress = InetUtils.getByStringIpAddress(x.getServiceIp());

        //service provider ip address + port
        String clientTag = UniqueInetTagProducer.get4Client(byStringIpAddress, x.getServicePort());

        ClientServiceProvider existProvider = portProtectorMap.get(clientTag);
        if (existProvider != null) {
            logger.error("the service " + clientTag + " has been register in this map");

            //移除所有已建立的本地连接
            existProvider.releaseAllRelatedResources();
            return;
        }

        logger.debug("init local service:" + x + "--->" + clientTag);

        //创建新的服务提供者
        ClientServiceProvider clientServiceProvider = new ClientServiceProvider(x.getServicePort(), x.getServiceIp());
        portProtectorMap.put(clientTag, clientServiceProvider);
    }

    public void destroyService(ClientServiceDescription x) {
        InetAddress byStringIpAddress = InetUtils.getByStringIpAddress(x.getServiceIp());
        String clientTag = UniqueInetTagProducer.get4Client(byStringIpAddress, x.getServicePort());
        ClientServiceProvider remove = portProtectorMap.remove(clientTag);
        if (remove == null) {
            logger.error("can not found service " + clientTag + " in local");
            return;
        } else {
            remove.releaseAllRelatedResources();
        }
    }




    public void successToConnectToServer() {
        connectionToServerState.set(true);
    }

    public void failToConnectToServer() {
        connectionToServerState.set(false);
    }

    public boolean getCurrentClientConnectionState() {
        return connectionToServerState.get();
    }
}
