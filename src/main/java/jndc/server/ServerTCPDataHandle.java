package jndc.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.utils.ByteBufUtil4V;
import jndc.utils.InetUtils;
import jndc.utils.UniqueInetTagProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ServerTCPDataHandle extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String NAME = "NDC_SERVER_TCP_DATA_HANDLE";

    private ChannelHandlerContext ctx;

    private ServerPortProtector.InnerActiveCallBack innerActiveCallBack;

    public ServerTCPDataHandle(ServerPortProtector.InnerActiveCallBack innerActiveCallBack) {
        this.innerActiveCallBack = innerActiveCallBack;
    }


    /**
     * tcp active
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        InetSocketAddress socketAddress = (InetSocketAddress) this.ctx.channel().remoteAddress();
        String server = UniqueInetTagProducer.get4Server(socketAddress);
        this.innerActiveCallBack.register(server, this);

        logger.info("open face tcp "+this.ctx.channel().remoteAddress());

        Channel channel = ctx.channel();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();

        //发送消息
        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.of(remoteAddress.getAddress(), InetUtils.localInetAddress, remoteAddress.getPort(), localAddress.getPort(), NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.TCP_ACTIVE);
        ndcMessageProtocol.setData(NDCMessageProtocol.ACTIVE_MESSAGE);
        UniqueBeanManage.getBean(NDCServerConfigCenter.class).addMessageToSendQueue(ndcMessageProtocol);


    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        Channel channel = ctx.channel();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();

        //发送消息
        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.of(remoteAddress.getAddress(), InetUtils.localInetAddress, remoteAddress.getPort(), localAddress.getPort(), NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.CONNECTION_INTERRUPTED);
        ndcMessageProtocol.setData(NDCMessageProtocol.BLANK);
        UniqueBeanManage.getBean(NDCServerConfigCenter.class).addMessageToSendQueue(ndcMessageProtocol);
        logger.info("server send interrupt signal ");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = ByteBufUtil4V.readWithRelease(byteBuf);


        Channel channel = ctx.channel();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();

        //发送消息

        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.of(remoteAddress.getAddress(), InetUtils.localInetAddress, remoteAddress.getPort(), localAddress.getPort(), NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.TCP_DATA);
        ndcMessageProtocol.setData(bytes);
        UniqueBeanManage.getBean(NDCServerConfigCenter.class).addMessageToSendQueue(ndcMessageProtocol);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client get a exception: "+cause);
    }

    /**
     * face tcp receive message
     *
     * @param byteBuf
     */
    public void receiveMessage(ByteBuf byteBuf) {
        this.ctx.writeAndFlush(byteBuf);
    }

    public void releaseRelatedResources() {
        if (this.ctx!=null){
            this.ctx.close();
            logger.info("close face tcp "+this.ctx.channel().remoteAddress());
            this.ctx=null;
        }

    }


}
