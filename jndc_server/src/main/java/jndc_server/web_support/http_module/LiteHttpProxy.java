package jndc_server.web_support.http_module;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.ScheduledFuture;
import javafx.util.Callback;
import jndc.core.NettyComponentConfig;
import jndc.utils.ByteBufUtil4V;
import jndc.utils.InetUtils;
import jndc_server.web_support.model.data_object.HttpHostRoute;
import jndc_server.web_support.utils.BlockValueFeature;

import java.util.concurrent.*;


public class LiteHttpProxy {


    private static final EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();
    private FullHttpRequest fullHttpRequest;
    private HttpHostRoute httpHostRoute;
    private BlockValueFeature completeFeature;


    public LiteHttpProxy(HttpHostRoute httpHostRoute, FullHttpRequest fullHttpRequest) {
        this.httpHostRoute = httpHostRoute;
        this.fullHttpRequest = fullHttpRequest;
    }

    public void release() {
        fullHttpRequest = null;
        httpHostRoute = null;
    }


    public BlockValueFeature<FullHttpResponse> forward() {
        LiteHttpProxy liteHttpProxy = this;
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();


                pipeline.addLast(new HttpClientCodec());
                pipeline.addLast(new HttpObjectAggregator(2 * 1024 * 1024));//限制缓冲最大值为2mb
                pipeline.addLast(new LiteProxyHandle(liteHttpProxy));
            }
        };


        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(channelInitializer);
        ChannelFuture sync = null;
        try {
          sync = b.connect(InetUtils.getInetAddressByHost(httpHostRoute.getForwardHost()), httpHostRoute.getForwardPort()).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Channel channel = sync.channel();
        channel.writeAndFlush(fullHttpRequest);
        completeFeature = new BlockValueFeature<>();
        return completeFeature;

    }


    public void writeData(FullHttpResponse data) {
        this.completeFeature.complete(data);
    }


}
