package jndc_server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jndc.core.NDCMessageProtocol;
import jndc.core.NettyComponentConfig;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.utils.UniqueInetTagProducer;
import jndc_server.core.filter.CustomRulesFilter;
import jndc_server.databases_object.ServerPortBind;
import jndc_server.web_support.core.MessageNotificationCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * port bind context
 */
public class ServerPortProtector {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int port;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroup;

    private LocalTime startDatePoint;

    private LocalTime endDatePoint;


    private Map<String, ServerTCPDataHandle> faceTCPMap = new ConcurrentHashMap<>();//store tcp


    private ReentrantReadWriteLock reentrantLock=new ReentrantReadWriteLock();


    public ServerPortProtector(int port) {
        this.port = port;
    }

    public boolean checkBetweenEnableTimeRange() {
        LocalTime now = LocalTime.now();
        return now.isAfter(startDatePoint) && now.isBefore(endDatePoint);
    }

    public void parseEnableDateRange(String dateRange) {
        try {
            String[] split = dateRange.split(",");
            String startString = split[0];
            String endString = split[1];
            startDatePoint = LocalTime.parse(startString);
            endDatePoint = LocalTime.parse(endString);
            logger.debug("set enable range between " + startDatePoint + " to " + endDatePoint);
        } catch (Exception e) {
            //todo if get any exception, the port will reject all requests

            logger.error("parse the enable date range error,to ensure security, the port will reject all requests");
            startDatePoint = LocalTime.parse("00:00:00");
            endDatePoint = LocalTime.parse("00:00:00");
        }

    }

    public boolean start() {
        InnerActiveCallBack innerActiveCallBack = (uniqueTag, serverTCPDataHandle) -> {
            faceTCPMap.put(uniqueTag, serverTCPDataHandle);
        };


        //create  Initializer
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addFirst(CustomRulesFilter.NAME, CustomRulesFilter.STATIC_INSTANCE);
                pipeline.addAfter(CustomRulesFilter.NAME, ServerTCPDataHandle.NAME, new ServerTCPDataHandle(innerActiveCallBack));
            }
        };


        eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(new InetSocketAddress(port))//　
                .childHandler(channelInitializer);

        try {
            serverBootstrap.bind().sync();
            return true;
        } catch (Exception e) {
            logger.error("bind server port:" + port + " fail cause:" + e);
            return false;
        }
    }

    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol) {
        InetAddress remoteInetAddress = ndcMessageProtocol.getRemoteInetAddress();
        int remotePort = ndcMessageProtocol.getRemotePort();
        String client = UniqueInetTagProducer.get4Client(remoteInetAddress, remotePort);
        ServerTCPDataHandle serverTCPDataHandle = faceTCPMap.get(client);
        if (serverTCPDataHandle == null) {
            //todo drop
            logger.error("not found the tcp connection:" + client);
            return;
        }

        serverTCPDataHandle.receiveMessage(Unpooled.copiedBuffer(ndcMessageProtocol.getData()));


    }

    public void resetAllConnection() {
        Map<String, ServerTCPDataHandle> backUp = this.faceTCPMap;
        this.faceTCPMap = new ConcurrentHashMap<>();
        backUp.forEach((k, v) -> {
            v.releaseRelatedResources();
        });
    }

    /**
     * close the port listener and interrupt all tcp connection
     */
    public void releaseRelatedResources() {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully().addListener(x -> {
                if (x.isSuccess()) {
                    logger.debug(" release serverPortProtector for port " + port + " success");
                    eventLoopGroup = null;
                } else {
                    logger.error("serverPortProtector for port " + port + " release fail");
                }
            });
        }

        if (faceTCPMap != null) {
            faceTCPMap.forEach((k, v) -> {
                v.releaseRelatedResources();
            });
            faceTCPMap = null;
        }
        serverBootstrap = null;


        //remove from map
        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        Map<Integer, ServerPortBindContext> tcpRouter = bean.getTcpRouter();
        tcpRouter.remove(port);


        //update db info
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        dbWrapper.customExecute("update server_port_bind set portEnable=0 where port=?", port);


        //notice refresh data
        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
        messageNotificationCenter.dateRefreshMessage("serverPortList");
    }


    /**
     * interrupt one tcp connection
     *
     * @param ndcMessageProtocol
     */
    public void connectionInterrupt(NDCMessageProtocol ndcMessageProtocol) {
        InetAddress remoteInetAddress = ndcMessageProtocol.getRemoteInetAddress();
        int remotePort = ndcMessageProtocol.getRemotePort();
        String client = UniqueInetTagProducer.get4Client(remoteInetAddress, remotePort);

        ServerTCPDataHandle serverTCPDataHandle = faceTCPMap.get(client);
        if (serverTCPDataHandle == null) {
            //todo drop
            logger.error("not found the tcp connection:" + client);
            return;
        }
        serverTCPDataHandle.releaseRelatedResources();

    }


    public interface InnerActiveCallBack {

        public void register(String uniqueTag, ServerTCPDataHandle serverTCPDataHandle);
    }


}
