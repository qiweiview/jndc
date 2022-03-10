package jndc_server.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jndc.utils.NettyContextUtils;
import jndc.utils.UUIDSimple;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 隧道上下文描述对象
 */
@Data
@Slf4j
public class ChannelHandlerContextHolder {

    private volatile boolean released = false;

    //心跳超时时间 5分钟
    private static final long HEART_BEAT_TIME_OUT = 5 * 60 * 1000;

    //客户端唯一编号
    private String clientId;

    //远程Ip
    private String contextIp;

    //远程端口
    private int contextPort;

    //最后心跳时间
    private long lastHearBeatTimeStamp;

    //隧道上下文
    private ChannelHandlerContext channelHandlerContext;

    //上下文中，注册的服务集合
    private List<TcpServiceDescriptionOnServer> tcpServiceDescriptions = new ArrayList<>();

    /**
     * 构造 隧道上下文描述对象
     *
     * @param channelId 客户端唯一编号
     */
    public ChannelHandlerContextHolder(String channelId) {
        this.clientId = channelId;
    }

    /**
     * 释放上下文描述对象
     */
    public void releaseRelatedResources() {
        if (released) {
            return;
        }


        //获取断开隧道标识
        String fingerprintFromContext = NettyContextUtils.getFingerprintFromContext(channelHandlerContext);


        if (tcpServiceDescriptions != null) {
            tcpServiceDescriptions.forEach(x -> {
                //检测释放服务
                x.releaseRelatedResourcesWithCheck(fingerprintFromContext);
            });
        }

        //关闭上下文
        channelHandlerContext.close();

        //释放对象
        channelHandlerContext = null;
        tcpServiceDescriptions = null;


        released = true;

    }

    public String getFingerprint() {
        return contextIp + contextPort;
    }

    /**
     * @param inactive
     * @return
     */
    public boolean checkSame(ChannelHandlerContext inactive) {
        //传入上下文ip+端口
        String inactiveContextStr = NettyContextUtils.getFingerprintFromContext(inactive);

        //当前上下文ip+端口
        String currentContextStr = NettyContextUtils.getFingerprintFromContext(channelHandlerContext);

        return currentContextStr.equals(inactiveContextStr);
    }


    /**
     * 解析上下文基础信息
     */
    private void parseBaseInfo() {
        Channel channel = this.channelHandlerContext.channel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        this.contextIp = socketAddress.getHostString();
        this.contextPort = socketAddress.getPort();

    }

    public int serviceNum() {
        return tcpServiceDescriptions == null ? 0 : tcpServiceDescriptions.size();
    }


    public void setChannelHandlerContextWithParse(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;

        //解析上下文基础信息
        parseBaseInfo();
    }


    /**
     * 移除服务
     *
     * @param tcpServiceDescriptionOnServers
     */
    public void removeTcpServiceDescriptions(List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers) {
        Set<String> strings = tcpServiceDescriptionOnServers.stream().map(x -> x.getRouteTo()).collect(Collectors.toSet());

        //加索
        synchronized (ChannelHandlerContextHolder.class) {
            Iterator<TcpServiceDescriptionOnServer> iterator = tcpServiceDescriptions.iterator();
            while (iterator.hasNext()) {
                TcpServiceDescriptionOnServer tcpServiceDescriptionOnServer = iterator.next();
                if (strings.contains(tcpServiceDescriptionOnServer.getRouteTo())) {
                    //todo 命中
                    iterator.remove();

                    //释放服务
                    tcpServiceDescriptionOnServer.releaseRelatedResources();
                }
            }
        }

    }

    /**
     * 向上下文中添加服务
     *
     * @param tcpServiceDescriptionOnServers 注册的服务
     */
    public void addTcpServiceDescriptions(List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers) {

        //已有服务放入Set
        Set<String> strings = tcpServiceDescriptions.stream()
                .map(x -> x.getRouteTo())
                .collect(Collectors.toSet());

        //遍历新注册服务
        tcpServiceDescriptionOnServers.forEach(x -> {

            //获取路由规则
            String routeTo = x.getRouteTo();
            if (strings.contains(routeTo)) {
                //todo 服务已存在
                log.error("the service " + routeTo + "is exist");
            } else {
                x.setId(UUIDSimple.id());
                x.setBelongContext(channelHandlerContext);

                //添加到--->上下文中注册的服务
                tcpServiceDescriptions.add(x);
            }
        });
    }


    /**
     * 刷新心跳时间
     */
    public void refreshHeartBeatTimeStamp() {
        lastHearBeatTimeStamp = System.currentTimeMillis();
    }

    /**
     * @return true mean the connection is unreachable / return false mean the connection is still connected
     */
    public boolean checkUnreachable() {
        return lastHearBeatTimeStamp + HEART_BEAT_TIME_OUT < System.currentTimeMillis();
    }


    /**
     * 刷新context
     *
     * @param tcpServiceDescription
     */
    public void refreshContext(TcpServiceDescriptionOnServer tcpServiceDescription) {
        tcpServiceDescription.refreshContext(channelHandlerContext);
    }


}
