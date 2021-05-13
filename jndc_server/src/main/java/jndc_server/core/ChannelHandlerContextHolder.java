package jndc_server.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jndc.core.TcpServiceDescription;
import jndc.utils.UUIDSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;

/**
 *
 */
public class ChannelHandlerContextHolder {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final long  HEART_BEAT_TIME_OUT=5*60*1000;

    private String id;//the id for the channelHandlerContext

    private String contextIp;

    private int contextPort;

    private long lastHearBeatTimeStamp;

    private ChannelHandlerContext channelHandlerContext;

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

    public boolean sameId(String del) {
        return id.equals(del);
    }


    /**
     * the id of channel from client
     * @return
     */
    public String getId() {
        return id;
    }

    private void parseBaseInfo() {
        Channel channel = this.channelHandlerContext.channel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        contextIp = socketAddress.getHostString();
        contextPort = socketAddress.getPort();

    }

    public int serviceNum() {
        return tcpServiceDescriptions == null ? 0 : tcpServiceDescriptions.size();
    }



    /* ------------------getter setter------------------ */

    public long getLastHearBeatTimeStamp() {
        return lastHearBeatTimeStamp;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public String getContextIp() {
        return contextIp;
    }

    public int getContextPort() {
        return contextPort;
    }

    public void setTcpServiceDescriptions(List<TcpServiceDescriptionOnServer> tcpServiceDescriptions) {
        this.tcpServiceDescriptions = tcpServiceDescriptions;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
        parseBaseInfo();
    }

    public List<TcpServiceDescriptionOnServer> getTcpServiceDescriptions() {
        return tcpServiceDescriptions;
    }


    public void removeTcpServiceDescriptions(List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers) {
        Set<String> strings = new HashSet<>();
        tcpServiceDescriptionOnServers.forEach(x -> {
            x.setBelongContextIp(contextIp);
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

    public void addTcpServiceDescriptions(List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers) {
        Set<String> strings = new HashSet<>();
        tcpServiceDescriptions.forEach(x -> {
            strings.add(x.getRouteTo());
        });
        tcpServiceDescriptionOnServers.forEach(x -> {
            x.setBelongContextIp(contextIp);
            String routeTo = x.getRouteTo();
            if (strings.contains(routeTo)) {
                logger.error("the service " + routeTo + "is exist");
            } else {
                x.setBelongContextIp(contextIp);
                x.setId(UUIDSimple.id());
                x.setBelongContext(channelHandlerContext);
                tcpServiceDescriptions.add(x);
            }
        });
    }


    public void refreshHeartBeatTimeStamp() {
        lastHearBeatTimeStamp = System.currentTimeMillis();
    }

    /**
     *
     * @return true mean the connection is unreachable / return false mean the connection is still connected
     *
     */
    public boolean checkUnreachable() {
        return lastHearBeatTimeStamp +HEART_BEAT_TIME_OUT < System.currentTimeMillis();
    }


}
