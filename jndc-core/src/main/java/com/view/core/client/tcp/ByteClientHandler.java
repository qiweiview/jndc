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

        NDCPacket ndcPacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
        if (GlobalBeanContext.NDC_CLIENT != null) {
            GlobalBeanContext.NDC_CLIENT.writePackage(ndcPacket);
        } else {
            log.warn("NDC_CLIENT is null");
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.debug("读取完成");
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
        log.warn("客户端断开");
    }

    public void write(byte[] bytes) {
        if (ctx != null) {
            ctx.writeAndFlush(bytes).addListener(future -> {
                if (future.isSuccess()) {
                    log.debug("TCP客户端写出:\n{}", new String(bytes));
                } else {
                    log.warn("数据发送失败");
                }
            });

        } else {
            log.warn("ChannelHandlerContext is null");
        }
    }
}
