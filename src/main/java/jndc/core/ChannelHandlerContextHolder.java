package jndc.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jndc.utils.LogPrint;
import jndc.utils.UUIDSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 */
public class ChannelHandlerContextHolder {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String id;//the id for the channelHandlerContext

    private String contextIp;

    private int contextPort;

    private ChannelHandlerContext channelHandlerContext;

    private List<TcpServiceDescription> tcpServiceDescriptions;

    public ChannelHandlerContextHolder() {
        id = UUIDSimple.id();
    }

    public void releaseRelatedResources() {
        channelHandlerContext.close().addListeners(x -> {
            logger.info(contextIp + " unRegister " + serviceNum() + " service");
        });
        channelHandlerContext = null;
        tcpServiceDescriptions = null;
    }

    public boolean contextBelong(ChannelHandlerContext inactive) {
        return inactive == channelHandlerContext;
    }

    public boolean sameId(String del) {
        return id.equals(del);
    }


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

    public String getContextIp() {
        return contextIp;
    }

    public int getContextPort() {
        return contextPort;
    }

    public void setTcpServiceDescriptions(List<TcpServiceDescription> tcpServiceDescriptions) {
        if (null != contextIp) {
            tcpServiceDescriptions.forEach(x -> {
                x.setBelongContextIp(contextIp);
            });
        }
        this.tcpServiceDescriptions = tcpServiceDescriptions;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
        parseBaseInfo();
    }

    public List<TcpServiceDescription> getTcpServiceDescriptions() {
        return tcpServiceDescriptions;
    }


    /* ------------------------重写分割线------------------------ */

//
//
//
//    private List<Integer>  serverPorts=new CopyOnWriteArrayList();//show which  face port  transfer data on this Context
//
//
//
//    //private List<ServerPortProtector> serverPortProtectorList= new CopyOnWriteArrayList();//be used when shut down ChannelHandlerContextHolder
//
//    private ServerPortProtector serverPortProtector;
//
//
//    public ChannelHandlerContextHolder(ChannelHandlerContext channelHandlerContext) {
//        this.id= UUIDSimple.id();
//        this.channelHandlerContext = channelHandlerContext;
//    }
//
//
//    public void addServerPortProtector(int port,ServerPortProtector serverPortProtector){
//        serverPortProtectorList.add(serverPortProtector);
//        serverPorts.add(port);
//
//    }
//
//
//
//    public ChannelHandlerContext getChannelHandlerContext() {
//        return channelHandlerContext;
//    }
//
//    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
//        this.channelHandlerContext = channelHandlerContext;
//    }
//
//    public List<ServerPortProtector> getServerPortProtectorList() {
//        return serverPortProtectorList;
//    }
//
//    public void setServerPortProtectorList(List<ServerPortProtector> serverPortProtectorList) {
//        this.serverPortProtectorList = serverPortProtectorList;
//    }
//
//
//    public List<Integer> getServerPorts() {
//        return serverPorts;
//    }
//
//
//    /**
//     * release bind  ServerPortProtectors
//     */
//    public void shutDownServerPortProtectors() {
//        serverPortProtectorList.forEach(x->{
//            x.releaseObject();
//        });
//
//    }


}
