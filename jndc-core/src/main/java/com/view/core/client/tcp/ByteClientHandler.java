package com.view.core.client.tcp;

import com.view.core.component.GlobalBeanContext;
import com.view.core.model.TCPDataTransport;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ByteClientHandler extends SimpleChannelInboundHandler<byte[]> {

    private TCPClient tcpClient;

    private ChannelHandlerContext ctx;


    public ByteClientHandler(TCPClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("收到本地服务channelActive消息：{}");
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        log.info("收到本地服务channelRead0消息：{}", new String(msg));
        TCPDataTransport tcpDataTransport = createTCPDataTransport();
        tcpDataTransport.setData(msg);

        NDCPacket ndcPacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
        GlobalBeanContext.NDC_CLIENT.writePackage(ndcPacket);
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


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    public void write(byte[] bytes) {
        if (ctx != null) {
            ctx.writeAndFlush(bytes);
        } else {
            log.warn("ChannelHandlerContext is null");
        }
    }
}
