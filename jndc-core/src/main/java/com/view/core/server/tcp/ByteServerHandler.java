package com.view.core.server.tcp;

import com.view.core.component.GlobalBeanContext;
import com.view.core.model.TCPDataTransport;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteServerHandler extends SimpleChannelInboundHandler<byte[]> {
    private String appServerSessionId;

    private TCPServer tcpServer;


    public ByteServerHandler(TCPServer tcpServer) {
        this.tcpServer = tcpServer;
    }

    /**
     * 客户端连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("收到客户端channelActive消息：{}");
        appServerSessionId = ctx.channel().id().asLongText();
        //注册会话
        tcpServer.registerSession(appServerSessionId, ctx);


        TCPDataTransport tcpDataTransport = createTCPDataTransport();

        //客户端信息
        String ndcClientId = tcpServer.getNdcClientId();

        //构建报文
        NDCPacket ndcPacket = NDCPacketBuilder.tcpActivePacket(tcpDataTransport);
        GlobalBeanContext.NDC_SERVER.write(ndcClientId, ndcPacket);


    }

    /**
     * 客户端断开连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    private TCPDataTransport createTCPDataTransport() {
        TCPDataTransport tcpDataTransport = new TCPDataTransport();

        //服务端信息
        tcpDataTransport.setNdcServerId(tcpServer.getNdcServerId());
        tcpDataTransport.setAppServerId(tcpServer.getAppServerId());
        tcpDataTransport.setAppServerSessionId(appServerSessionId);

        //客户端信息
        String ndcClientId = tcpServer.getNdcClientId();
        tcpDataTransport.setNdcClientId(ndcClientId);
        tcpDataTransport.setClientServiceId(tcpServer.getClientServiceId());

        return tcpDataTransport;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        log.info("收到客户端channelRead0消息：{}", msg);


        TCPDataTransport tcpDataTransport = createTCPDataTransport();
        tcpDataTransport.setData(msg);

        //客户端信息
        String ndcClientId = tcpServer.getNdcClientId();


        //构建报文
        NDCPacket ndcPacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
        GlobalBeanContext.NDC_SERVER.write(ndcClientId, ndcPacket);
    }
}
