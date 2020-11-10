package web.model.view_object;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.ChannelHandlerContextHolder;
import jndc.core.NDCMessageProtocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ChannelContextVO {

    private int usedServerPort;
    private int channelClientPort;
    private String channelClientIp;


    public static ChannelContextVO of(int usedServerPort,ChannelHandlerContext channelHandlerContext) {
        ChannelContextVO facePortVO = new ChannelContextVO();

        InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        facePortVO.setChannelClientPort(socketAddress.getPort());
        InetAddress localInetAddress = socketAddress.getAddress();
        String hostAddress = localInetAddress.getHostAddress();
        facePortVO.setChannelClientIp(hostAddress);
        facePortVO.setUsedServerPort(usedServerPort);
        return facePortVO;

    }


    public int getUsedServerPort() {
        return usedServerPort;
    }

    public void setUsedServerPort(int usedServerPort) {
        this.usedServerPort = usedServerPort;
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
