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
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * port bind context
 */
@Slf4j
public class ServerPortProtector {

    private volatile boolean released = false;

    private int port;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroup;

    private LocalTime startDatePoint;

    private LocalTime endDatePoint;

    //访问连接映射
    private Map<String, ServerTCPDataHandle> faceTCPMap = new ConcurrentHashMap<>();//store tcp


    public ServerPortProtector(int port) {
        this.port = port;
    }

    public boolean checkBetweenEnableTimeRange() {
        LocalTime now = LocalTime.now();
        return now.isAfter(startDatePoint) && now.isBefore(endDatePoint);
    }

    /**
     * 解析可用时间
     *
     * @param dateRange
     */
    public void parseEnableDateRange(String dateRange) {
        try {
            String[] split = dateRange.split(",");
            String startString = split[0];
            String endString = split[1];
            startDatePoint = LocalTime.parse(startString);
            endDatePoint = LocalTime.parse(endString);
            log.debug("set enable range between " + startDatePoint + " to " + endDatePoint);
        } catch (Exception e) {
            //todo if get any exception, the port will reject all requests

            log.error("parse the enable date range error,to ensure security, the port will reject all requests");
            startDatePoint = LocalTime.parse("00:00:00");
            endDatePoint = LocalTime.parse("00:00:00");
        }

    }

    /**
     * 启动端口监听器
     *
     * @return
     */
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
            log.error("bind server port:" + port + " fail cause:" + e);
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
            log.error("not found the tcp connection:" + client);
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
     * 释放端口监听器
     */
    public void releaseRelatedResources() {
        if (released) {
            //todo 已释放
            return;
        }

        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully().addListener(x -> {
                if (x.isSuccess()) {
                    log.debug(" release serverPortProtector for port " + port + " success");
                    eventLoopGroup = null;
                } else {
                    log.error("serverPortProtector for port " + port + " release fail");
                }
            });
        }

        if (faceTCPMap != null) {
            //循环该端口上的所有连接
            faceTCPMap.forEach((k, v) -> {
                //关闭往端口监听器建立的连接
                v.releaseRelatedResources();
            });
            faceTCPMap = null;
        }
        serverBootstrap = null;


        //获取注册中心
        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        //获取    --->   服务端端口号 ： 服务端口绑定上下文
        Map<Integer, ServerPortBindContext> tcpRouter = bean.getTcpRouter();

        //移除对应端口
        ServerPortBindContext remove = tcpRouter.remove(port);

        //释放 服务端口绑定上下文
        remove.releaseRelatedResources();


        //更新端口绑定信息
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        dbWrapper.customExecute("update server_port_bind set portEnable=0 where port=?", port);


        //同通刷新服务端口列表
        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
        messageNotificationCenter.dateRefreshMessage("serverPortList");

        released = true;
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
            log.error("not found the tcp connection:" + client);
            return;
        }
        serverTCPDataHandle.releaseRelatedResources();

    }


    /**
     * 注册事件
     */
    public interface InnerActiveCallBack {

        void register(String uniqueTag, ServerTCPDataHandle serverTCPDataHandle);
    }


}
