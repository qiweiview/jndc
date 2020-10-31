package jndc.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.client.JNDCClientConfigCenter;
import jndc.core.NDCMessageProtocol;
import jndc.core.ServerPortProtector;
import jndc.core.UniqueBeanManage;
import jndc.utils.ByteBufUtil4V;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;
import jndc.utils.UniqueInetTagProducer;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerTCPDataHandle extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext channelHandlerContext;


    private ServerPortProtector.InnerHandlerCallBack innerHandlerCallBack;

    public static final String NAME = "NDC_SERVER_TCP_DATA_HANDLE";

    private String uniqueTag;

    public ServerTCPDataHandle(ServerPortProtector.InnerHandlerCallBack innerHandlerCallBack) {
        this.innerHandlerCallBack = innerHandlerCallBack;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channelHandlerContext = ctx;
        Channel channel = ctx.channel();

        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress)channel.localAddress();

        InetAddress address = remoteAddress.getAddress();

        byte[] address1 = address.getAddress();

        if (address1.length>8){
            LogPrint.log("unSupport ipv6");
            ctx.writeAndFlush(Unpooled.copiedBuffer("unSupport ipv6".getBytes())).addListeners(ChannelFutureListener.CLOSE);
            return;
        }

        LogPrint.log(remoteAddress+"接入");


        uniqueTag = UniqueInetTagProducer.get4Server(remoteAddress);

        //register tcp
        innerHandlerCallBack.registerHandler(uniqueTag, this);



        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, remoteAddress.getPort(), localAddress.getPort(), innerHandlerCallBack.getLocalPort(), NDCMessageProtocol.TCP_ACTIVE);
        ndcMessageProtocol.setData(NDCMessageProtocol.ACTIVE_MESSAGE);
        UniqueBeanManage.getBean(NDCServerConfigCenter.class).addMessageToSendQueue(ndcMessageProtocol);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = ByteBufUtil4V.readWithRelease(byteBuf);


        Channel channel = ctx.channel();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress)channel.localAddress();

        //发送消息

        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, remoteAddress.getPort(), localAddress.getPort(), innerHandlerCallBack.getLocalPort(), NDCMessageProtocol.TCP_DATA);
        ndcMessageProtocol.setData(bytes);
        UniqueBeanManage.getBean(NDCServerConfigCenter.class).addMessageToSendQueue(ndcMessageProtocol);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress)channel.localAddress();

        LogPrint.log(remoteAddress+"断开");

        //发送消息
        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, remoteAddress.getPort(), localAddress.getPort(), innerHandlerCallBack.getLocalPort(), NDCMessageProtocol.CONNECTION_INTERRUPTED);
        ndcMessageProtocol.setData("connection lose".getBytes());
        UniqueBeanManage.getBean(NDCServerConfigCenter.class).addMessageToSendQueue(ndcMessageProtocol);

        if (uniqueTag!=null){
            innerHandlerCallBack.unRegisterHandler(uniqueTag);
        }

        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       LogPrint.err("server face tcp get a unCatchable error cause:"+cause);
    }

    public void close() {
        if (channelHandlerContext != null) {
            channelHandlerContext.close();
        }
    }


    public void writeMessage(ByteBuf byteBuf) {
        channelHandlerContext.writeAndFlush(byteBuf);
    }

}
