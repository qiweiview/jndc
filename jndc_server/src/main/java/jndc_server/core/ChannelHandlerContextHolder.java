package jndc_server.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jndc.utils.UUIDSimple;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * 隧道上下文描述对象
 */
@Data
public class ChannelHandlerContextHolder {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final long HEART_BEAT_TIME_OUT = 5 * 60 * 1000;

    //客户端唯一编号
    private String id;

    private String contextIp;

    private int contextPort;

    private long lastHearBeatTimeStamp;

    private ChannelHandlerContext channelHandlerContext;

    //上下文中注册的服务
    private List<TcpServiceDescriptionOnServer> tcpServiceDescriptions = new ArrayList<>();

    public ChannelHandlerContextHolder(String channelId) {
        id = channelId;
    }

    public void releaseRelatedResources() {

        logger.debug(contextIp + " unRegister " + serviceNum() + " service");

        if (tcpServiceDescriptions != null) {
            tcpServiceDescriptions.forEach(x -> {
                //TcpServiceDescription
                x.releaseRelatedResources();
            });
        }


        channelHandlerContext = null;
        tcpServiceDescriptions = null;


    }

    public boolean contextBelong(ChannelHandlerContext inactive) {
        return inactive == channelHandlerContext;
    }


    /**
     * 解析上下文基础信息
     */
    private void parseBaseInfo() {
        Channel channel = this.channelHandlerContext.channel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        contextIp = socketAddress.getHostString();
        contextPort = socketAddress.getPort();

    }

    public int serviceNum() {
        return tcpServiceDescriptions == null ? 0 : tcpServiceDescriptions.size();
    }


    public void setChannelHandlerContextWithParse(ChannelHandlerContext channelHandlerContext) {
        setChannelHandlerContext(channelHandlerContext);

        //解析上下文基础信息
        parseBaseInfo();
    }


    public void removeTcpServiceDescriptions(List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers) {
        Set<String> strings = new HashSet<>();
        tcpServiceDescriptionOnServers.forEach(x -> {
            strings.add(x.getRouteTo());
        });

        //lock
        synchronized (ChannelHandlerContextHolder.class) {
            Iterator<TcpServiceDescriptionOnServer> iterator = tcpServiceDescriptions.iterator();
            while (iterator.hasNext()) {
                TcpServiceDescriptionOnServer next = iterator.next();
                if (strings.contains(next.getRouteTo())) {
                    iterator.remove();
                    next.releaseRelatedResources();
                }
            }
        }

    }

    /**
     * @param tcpServiceDescriptionOnServers
     */
    public void addTcpServiceDescriptions(List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers) {

        //遍历已有服务
        Set<String> strings = new HashSet<>();
        tcpServiceDescriptions.forEach(x -> {
            strings.add(x.getRouteTo());
        });

        //遍历新注册服务
        tcpServiceDescriptionOnServers.forEach(x -> {

            //获取路由规则
            String routeTo = x.getRouteTo();
            if (strings.contains(routeTo)) {
                logger.error("the service " + routeTo + "is exist");
            } else {
                x.setId(UUIDSimple.id());
                x.setBelongContext(channelHandlerContext);

                //添加到--->上下文中注册的服务
                tcpServiceDescriptions.add(x);
            }
        });
    }


    public void refreshHeartBeatTimeStamp() {
        lastHearBeatTimeStamp = System.currentTimeMillis();
    }

    /**
     * @return true mean the connection is unreachable / return false mean the connection is still connected
     */
    public boolean checkUnreachable() {
        return lastHearBeatTimeStamp + HEART_BEAT_TIME_OUT < System.currentTimeMillis();
    }


}
