package cn.view.jndc.server_sv.core.port_app;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.utils.ByteBufUtil4V;
import jndc.utils.InetUtils;
import jndc.utils.UniqueInetTagProducer;
import jndc_server.core.NDCServerConfigCenter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 端口连接事件处理器
 */
@Slf4j
public class ServerTCPDataHandle extends ChannelInboundHandlerAdapter {


    public static final String NAME = "NDC_SERVER_TCP_DATA_HANDLE";

    private ChannelHandlerContext ctx;

    //注册回调
    private ServerPortProtector.InnerActiveCallBack innerActiveCallBack;

    public ServerTCPDataHandle(ServerPortProtector.InnerActiveCallBack innerActiveCallBack) {
        this.innerActiveCallBack = innerActiveCallBack;
    }


    /**
     * tcp 连接连通
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        InetSocketAddress socketAddress = (InetSocketAddress) this.ctx.channel().remoteAddress();
        String server = UniqueInetTagProducer.get4Server(socketAddress);
        this.innerActiveCallBack.register(server, this);


        log.debug("open face tcp " + this.ctx.channel().remoteAddress());

        Channel channel = ctx.channel();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();


        int serverPort = localAddress.getPort();


        //发送消息
        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.of(remoteAddress.getAddress(), InetUtils.localInetAddress, remoteAddress.getPort(), serverPort, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.TCP_ACTIVE);
        ndcMessageProtocol.setData(NDCMessageProtocol.ACTIVE_MESSAGE);
        UniqueBeanManage.getBean(NDCServerConfigCenter.class).addMessageToSendQueue(ndcMessageProtocol);


    }


    /**
     * tcp 连接断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        Channel channel = ctx.channel();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();


        int serverPort = localAddress.getPort();

        //发送消息
        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.of(remoteAddress.getAddress(), InetUtils.localInetAddress, remoteAddress.getPort(), serverPort, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.CONNECTION_INTERRUPTED);
        ndcMessageProtocol.setData(NDCMessageProtocol.BLANK);
        UniqueBeanManage.getBean(NDCServerConfigCenter.class).addMessageToSendQueue(ndcMessageProtocol);
        log.debug("server send interrupt signal ");
    }

    /**
     * 数据包到达
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = ByteBufUtil4V.readWithRelease(byteBuf);


        Channel channel = ctx.channel();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();

        int serverPort = localAddress.getPort();

        //发送消息
        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.of(remoteAddress.getAddress(), InetUtils.localInetAddress, remoteAddress.getPort(), serverPort, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.TCP_DATA);
        ndcMessageProtocol.setData(bytes);

        //获取配置中心
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);


        //将消息放入发送队列
        ndcServerConfigCenter.addMessageToSendQueue(ndcMessageProtocol);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("tcp server get a exception: " + cause);
    }

    /**
     * 接收到消息
     *
     * @param byteBuf
     */
    public void receiveMessage(ByteBuf byteBuf) {
        this.ctx.writeAndFlush(byteBuf);
    }

    /**
     * 关闭往端口监听器建立的连接
     */
    public void releaseRelatedResources() {
        if (this.ctx != null) {
            //关闭往端口监听器建立的连接
            this.ctx.close();
            log.debug("close face tcp " + this.ctx.channel().remoteAddress());
            //释放引用
            this.ctx = null;
        }

    }


}
