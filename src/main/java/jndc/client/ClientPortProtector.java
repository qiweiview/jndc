package jndc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NDCMessageProtocol;
import jndc.core.NettyComponentConfig;
import jndc.core.PortProtector;
import jndc.core.UniqueBeanManage;
import jndc.core.config.ClientConfig;
import jndc.core.config.UnifiedConfiguration;
import jndc.exception.ConnectionOpenFailException;
import jndc.server.NDCServerConfigCenter;
import jndc.utils.UniqueInetTagProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPortProtector implements PortProtector {

    private   final Logger logger = LoggerFactory.getLogger(getClass());
    
    private int port;//this ClientPortProtector focus port


    private EventLoopGroup eventLoopGroup=NettyComponentConfig.getNioEventLoopGroup();

    private volatile boolean appRunnable = false;

    private Map<String, ClientTCPDataHandle> faceTCPMap = new ConcurrentHashMap<>();//store tcp


    public ClientPortProtector(int port) {
        this.port = port;
    }

    @Override
    public void start(NDCMessageProtocol registerMessage, NDCServerConfigCenter ndcServerConfigCenter) {

    }

    @Override
    public void shutDownBeforeCreate() {

    }


    public void shutDown(NDCMessageProtocol ndcMessageProtocol) {
        String clientTag = UniqueInetTagProducer.get4Client(ndcMessageProtocol.getRemoteInetAddress(),ndcMessageProtocol.getRemotePort());
        ClientTCPDataHandle clientTCPDataHandle = faceTCPMap.get(clientTag);

        if (clientTCPDataHandle == null) {
           //do nothing
        }else {
            faceTCPMap.remove(clientTag);
            clientTCPDataHandle.close();
            logger.info("local ClientPortProtector closed:"+clientTag);
        }
    }

    @Override
    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol) {


        String clientTag = UniqueInetTagProducer.get4Client(ndcMessageProtocol.getRemoteInetAddress(),ndcMessageProtocol.getRemotePort());
        ClientTCPDataHandle clientTCPDataHandle = faceTCPMap.get(clientTag);

        if (clientTCPDataHandle == null) {
            clientTCPDataHandle = startInnerBootstrap(ndcMessageProtocol);
            if (clientTCPDataHandle ==null){
                //todo start fail
                throw new ConnectionOpenFailException();
            }
            faceTCPMap.put(clientTag, clientTCPDataHandle);
        }


        //can replace with Arrays.compare in jdk 9
        if ( Arrays.equals(NDCMessageProtocol.ACTIVE_MESSAGE,ndcMessageProtocol.getData())){
            //todo ignore active message
            return;
        }
        clientTCPDataHandle.writeMessage(Unpooled.copiedBuffer(ndcMessageProtocol.getData()));
    }


    /**
     * start a netty client to localApp
     * @param ndcMessageProtocol
     * @return
     */
    private ClientTCPDataHandle startInnerBootstrap(NDCMessageProtocol ndcMessageProtocol) {
        ClientTCPDataHandle clientTCPDataHandle = new ClientTCPDataHandle(ndcMessageProtocol);

        Bootstrap b = new Bootstrap();
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst(ClientTCPDataHandle.NAME, clientTCPDataHandle);


            }
        };

        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)//
                .handler(channelInitializer);


        UnifiedConfiguration bean = UniqueBeanManage.getBean(UnifiedConfiguration.class);
        ClientConfig clientConfig = bean.getClientConfig();
        Map<Integer, InetSocketAddress> clientPortMappingMap = clientConfig.getClientPortMappingMap();
        InetSocketAddress inetSocketAddress = clientPortMappingMap.get(ndcMessageProtocol.getLocalPort());
        if (inetSocketAddress==null){
            //todo empty port mapping
            return null;
        }

        ChannelFuture connect = b.connect(inetSocketAddress);
        try {
            connect.sync();//block
            logger.debug("local app connect success");
            return clientTCPDataHandle;
        } catch (Exception e) {
            //todo connect error
            return null;
        }
    }


}
