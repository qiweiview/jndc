package jndc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jndc.core.NDCMessageProtocol;
import jndc.core.NettyComponentConfig;
import jndc.core.PortProtector;
import jndc.server.NDCServerConfigCenter;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;
import jndc.utils.UniqueInetTagProducer;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPortProtector implements PortProtector {


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
    public void shutDown() {

    }


    public void shutDown(NDCMessageProtocol ndcMessageProtocol) {
        String clientTag = UniqueInetTagProducer.get4Client(ndcMessageProtocol.getRemoteInetAddress(),ndcMessageProtocol.getRemotePort());
        ClientTCPDataHandle clientTCPDataHandle = faceTCPMap.get(clientTag);

        if (clientTCPDataHandle == null) {
           //do nothing
        }else {
            faceTCPMap.remove(clientTag);
            clientTCPDataHandle.close();
            LogPrint.log("local ClientPortProtector closed:"+clientTag);
        }
    }

    @Override
    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol) {


        String clientTag = UniqueInetTagProducer.get4Client(ndcMessageProtocol.getRemoteInetAddress(),ndcMessageProtocol.getRemotePort());
        ClientTCPDataHandle clientTCPDataHandle = faceTCPMap.get(clientTag);

        if (clientTCPDataHandle == null) {
            clientTCPDataHandle = startInnerBootstrap(ndcMessageProtocol);
            if (clientTCPDataHandle==null){
                //todo start fail
                return;
            }
            faceTCPMap.put(clientTag, clientTCPDataHandle);
        }

        if (Arrays.compare(NDCMessageProtocol.ACTIVE_MESSAGE,ndcMessageProtocol.getData())==0){
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
                .channel(NioSocketChannel.class)////　❸ 指定所使用的NIO传输Channel
                .handler(channelInitializer);

        InetSocketAddress localInetAddress = InetUtils.getLocalInetAddress(ndcMessageProtocol.getLocalPort());
        ChannelFuture connect = b.connect(localInetAddress);
        try {
            connect.sync();//block
            return clientTCPDataHandle;
        } catch (InterruptedException e) {
            //todo connect error
            return null;
        }
    }


}
