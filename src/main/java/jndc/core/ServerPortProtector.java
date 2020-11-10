package jndc.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jndc.server.NDCServerConfigCenter;
import jndc.server.ServerTCPDataHandle;
import jndc.utils.LogPrint;
import jndc.utils.UniqueInetTagProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerPortProtector  implements PortProtector{

    private  final  Logger logger = LoggerFactory.getLogger(getClass());
    
    private NDCMessageProtocol registerMessage;

    private NDCServerConfigCenter ndcServerConfigCenter;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroup;

    private volatile boolean appRunnable = false;

    private Map<String, ServerTCPDataHandle> faceTCPMap = new ConcurrentHashMap<>();//store tcp


    public ServerPortProtector() {
    }

    /**
     * 启动
     *
     * @param registerMessage
     */
    @Override
    public void start(NDCMessageProtocol registerMessage, NDCServerConfigCenter ndcServerConfigCenter) {
        if (appRunnable) {//just run once
            return;
        }

        this.ndcServerConfigCenter = ndcServerConfigCenter;
        this.registerMessage=registerMessage;


        //create  Initializer
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();


                //the callback  for connecting and closing event
                InnerHandlerCallBack innerHandlerCallBack = new InnerHandlerCallBack() {
                    @Override
                    public void registerHandler(String uniqueTag, ServerTCPDataHandle serverTCPDataHandle) {
                        ServerTCPDataHandle serverTCPDataHandle1 = faceTCPMap.get(uniqueTag);
                        if (serverTCPDataHandle1 != null) {
                            //todo impossible to this ,but just in case
                            serverTCPDataHandle1.close();
                        }
                        faceTCPMap.put(uniqueTag, serverTCPDataHandle);
                    }

                    @Override
                    public void unRegisterHandler(String uniqueTag) {
                        faceTCPMap.remove(uniqueTag);
                    }

                    @Override
                    public int getLocalPort() {
                        return registerMessage.getLocalPort();
                    }

                };

                //the handle of tcp data from user client
                ServerTCPDataHandle serverTCPDataHandle = new ServerTCPDataHandle(innerHandlerCallBack);


                pipeline.addFirst(IPFilter.NAME, IPFilter.STATIC_INSTANCE);
                pipeline.addAfter(IPFilter.NAME,ServerTCPDataHandle.NAME, serverTCPDataHandle);
            }
        };


        int serverPort = this.registerMessage.getServerPort();


        eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(new InetSocketAddress(serverPort))//　
                .childHandler(channelInitializer);

        serverBootstrap.bind().addListener(x -> {
            try{
                x.get();
                logger.info("bind map port:" + serverPort);
                appRunnable = true;
                ndcServerConfigCenter.registerPortProtector(serverPort, this);
            }catch (Exception e){
                e.printStackTrace();
                logger.error("port listen fail cause："+e);
            }
        });
    }


    /**
     * 关闭
     */
    @Override
    public void shutDownBeforeCreate() {
        int serverPort = this.registerMessage.getServerPort();
        ndcServerConfigCenter.unRegisterPortProtector(serverPort);
        eventLoopGroup.shutdownGracefully().addListener(x -> {
            logger.info("shut down server port:" + serverPort);
            appRunnable = false;
        });
    }

    @Override
    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol) {


        String s = UniqueInetTagProducer.get4Server(ndcMessageProtocol.getRemoteInetAddress(),ndcMessageProtocol.getRemotePort());
        ServerTCPDataHandle serverTCPDataHandle = faceTCPMap.get(s);
        if (serverTCPDataHandle == null) {
            //todo drop message
            logger.debug("drop message with tag "+s);
        } else {
            byte[] data = ndcMessageProtocol.getData();
            ByteBuf byteBuf = Unpooled.copiedBuffer(data);
            serverTCPDataHandle.writeMessage(byteBuf);
        }

    }

    public void releaseObject(){
        shutDownAllTcpConnection();
        sayGoodByeToEveryOne();
    }

    private void sayGoodByeToEveryOne(){
        int serverPort = this.registerMessage.getServerPort();
        ndcServerConfigCenter.unRegisterPortProtector(serverPort);
        eventLoopGroup.shutdownGracefully().addListener(x->{
            logger.info("shut down face port "+registerMessage.getServerPort());
            registerMessage=null;
            ndcServerConfigCenter=null;
            serverBootstrap=null;
            eventLoopGroup=null;
            faceTCPMap=null;
        });
    }


    private void shutDownAllTcpConnection() {

        //remove safety
        Set<Map.Entry<String, ServerTCPDataHandle>> entries = faceTCPMap.entrySet();
        Iterator<Map.Entry<String, ServerTCPDataHandle>> iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<String, ServerTCPDataHandle> next = iterator.next();
            next.getValue().close();
            iterator.remove();
            logger.debug("interrupt face connection port:"+next.getKey());
        }
    }

    public void shutDownTcpConnection(NDCMessageProtocol ndcMessageProtocol) {
        String s = UniqueInetTagProducer.get4Server(ndcMessageProtocol.getRemoteInetAddress(),ndcMessageProtocol.getRemotePort());
        ServerTCPDataHandle serverTCPDataHandle = faceTCPMap.get(s);
        if (serverTCPDataHandle == null) {
            //todo do nothing
        } else {
            faceTCPMap.remove(s);
            serverTCPDataHandle.close();
            logger.debug("close face connection cause local connection interrupted:"+s);
        }
    }

    public interface InnerHandlerCallBack {
        public void registerHandler(String uniqueTag, ServerTCPDataHandle serverTCPDataHandle);

        public void unRegisterHandler(String uniqueTag);

        public int getLocalPort();
    }

    public NDCMessageProtocol getRegisterMessage() {
        return registerMessage;
    }
}
