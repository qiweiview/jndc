package web.model.view_object;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.ChannelHandlerContextHolder;
import jndc.core.NDCMessageProtocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ChannelContextVO {

    private String channelId;

    private String usedServerPorts;

    private int channelClientPort;

    private String channelClientIp;


    public String uniqueTag(){
        return channelClientIp+channelClientPort;
    }

    public void mergeUsedServerPort(String port){
        setUsedServerPorts(getUsedServerPorts()+","+port);
    }

    public static ChannelContextVO of(int usedServerPort,ChannelHandlerContext channelHandlerContext) {
        ChannelContextVO facePortVO = new ChannelContextVO();

        InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        facePortVO.setChannelClientPort(socketAddress.getPort());
        InetAddress localInetAddress = socketAddress.getAddress();
        String hostAddress = localInetAddress.getHostAddress();
        facePortVO.setChannelClientIp(hostAddress);
        facePortVO.setUsedServerPorts(""+usedServerPort);
        return facePortVO;

    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUsedServerPorts() {
        return usedServerPorts;
    }

    public void setUsedServerPorts(String usedServerPorts) {
        this.usedServerPorts = usedServerPorts;
    }

    public int getChannelClientPort() {
        return channelClientPort;
    }

    public void setChannelClientPort(int channelClientPort) {
        this.channelClientPort = channelClientPort;
    }

    public String getChannelClientIp() {
        return channelClientIp;
    }

    public void setChannelClientIp(String channelClientIp) {
        this.channelClientIp = channelClientIp;
    }
}
