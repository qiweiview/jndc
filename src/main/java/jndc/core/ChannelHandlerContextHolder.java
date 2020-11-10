package jndc.core;

import io.netty.channel.ChannelHandlerContext;
import jndc.utils.UUIDSimple;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChannelHandlerContextHolder {
    private String id;

    private List<Integer>  serverPorts=new CopyOnWriteArrayList();//show which  face port  transfer data on this Context

    private ChannelHandlerContext channelHandlerContext;

    private List<ServerPortProtector> serverPortProtectorList= new CopyOnWriteArrayList();//be used when shut down ChannelHandlerContextHolder


    public ChannelHandlerContextHolder(ChannelHandlerContext channelHandlerContext) {
        this.id= UUIDSimple.id();
        this.channelHandlerContext = channelHandlerContext;
    }


    public void addServerPortProtector(int port,ServerPortProtector serverPortProtector){
        serverPortProtectorList.add(serverPortProtector);
        serverPorts.add(port);

    }



    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public List<ServerPortProtector> getServerPortProtectorList() {
        return serverPortProtectorList;
    }

    public void setServerPortProtectorList(List<ServerPortProtector> serverPortProtectorList) {
        this.serverPortProtectorList = serverPortProtectorList;
    }


    public List<Integer> getServerPorts() {
        return serverPorts;
    }


    /**
     * release bind  ServerPortProtectors
     */
    public void shutDownServerPortProtectors() {
        serverPortProtectorList.forEach(x->{
            x.releaseObject();
        });

    }

    public String getId() {
        return id;
    }
}
