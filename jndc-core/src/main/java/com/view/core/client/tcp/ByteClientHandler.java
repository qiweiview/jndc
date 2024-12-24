package com.view.core.client.tcp;

import com.view.core.component.GlobalBeanContext;
import com.view.core.model.DataSlot;
import com.view.core.model.TCPDataTransport;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Data
@Slf4j
public class ByteClientHandler extends SimpleChannelInboundHandler<byte[]> {

    private TCPClient tcpClient;

    private ChannelHandlerContext ctx;

    private List<DataSlot<byte[]>> slots;


    public ByteClientHandler(TCPClient tcpClient) {
        this.tcpClient = tcpClient;
        this.slots = tcpClient.getSlots();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        log.debug("TCP客户端channelActive");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        log.debug("TCP客户端读取数据\n{}", new String(msg));

        if (slots != null) {
            try {
                slots.forEach(x -> {
                    x.getConsumer().accept(msg);
                });
            } catch (Exception e) {
                log.warn("插槽处理异常", e);
            }
        }

        TCPDataTransport tcpDataTransport = createTCPDataTransport();
        tcpDataTransport.setData(msg);

        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();


        //组包，发送
        writePackageIntoChannel(tcpDataTransport,socketAddress);

    }

    /**
     * 向通道发送数据包
     *
     * @param tcpDataTransport
     */
    private void writePackageIntoChannel(TCPDataTransport tcpDataTransport, InetSocketAddress socketAddress) {
        NDCPacket ndcPacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
        ndcPacket.setLocalAddress(socketAddress.getAddress());
        ndcPacket.setLocalPort(socketAddress.getPort());

        log.debug("准备发送数据包：{}:{}", ndcPacket.getLocalAddress(),ndcPacket.getLocalPort());


        if (GlobalBeanContext.NDC_CLIENT != null) {
            GlobalBeanContext.NDC_CLIENT.writePackage(ndcPacket);
        } else {
            log.warn("NDC_CLIENT未初始化");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.error("{}:{}  {}:{}TCP客户端异常，准备通知远程断开连接:{}",
                localAddress.getHostName(),
                localAddress.getPort(),
                remoteAddress.getHostName(),
                remoteAddress.getPort(),
                cause.getMessage());
        //当作断开连接处理
        //channelInactive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //向通道发送关闭消息
        TCPDataTransport tcpDataTransport = createTCPDataTransport();

        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        //组包，发送
        writePackageIntoChannel(tcpDataTransport,socketAddress);
    }


    private TCPDataTransport createTCPDataTransport() {
        TCPDataTransport tcpDataTransport = new TCPDataTransport();

        //服务端信息

        tcpDataTransport.setAppServerId(tcpClient.getAppServerId());
        tcpDataTransport.setAppServerSessionId(tcpClient.getAppServerSessionId());

        //客户端信息

        tcpDataTransport.setClientServiceId(tcpClient.getClientServiceId());
        tcpDataTransport.setClientServiceSessionId(tcpClient.getClientServiceSessionId());

        return tcpDataTransport;
    }


    public void write(byte[] bytes) {
        if (ctx != null) {
            ctx.writeAndFlush(bytes);

        } else {
            log.warn("ChannelHandlerContext is null");
        }
    }
}
