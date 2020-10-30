package jndc.core;

import io.netty.channel.ChannelHandlerContext;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChannelHandlerContextHolder {
    private List<Integer>  serverPorts=new CopyOnWriteArrayList();
    private ChannelHandlerContext channelHandlerContext;
    private List<ServerPortProtector> serverPortProtectorList= new CopyOnWriteArrayList();




    public void addServerPortProtector(int port,ServerPortProtector serverPortProtector){
        serverPortProtectorList.add(serverPortProtector);
        serverPorts.add(port);

    }

    public ChannelHandlerContextHolder(ChannelHandlerContext channelHandlerContext) {

        this.channelHandlerContext = channelHandlerContext;
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

    public void shutDownServerPortProtectors() {
        serverPortProtectorList.forEach(x->{
            x.shutDownAllTcpConnection();
            x.sayGoodByeToEveryOne();
        });

    }
}
