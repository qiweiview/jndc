package jndc.web_support.http_module;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import jndc.core.NettyComponentConfig;
import jndc.utils.InetUtils;
import jndc.web_support.model.d_o.HttpHostRoute;
import jndc.web_support.utils.BlockValueFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.function.Consumer;


@Slf4j
@Data
public class LiteHttpProxy {

    private String id = UUID.randomUUID().toString();

    private volatile boolean canBeReUse;

    private volatile boolean hasBeenPut = false;

    //回收操作
    private Consumer<LiteHttpProxy> recycleOption;

    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();

    private BlockValueFeature<FullHttpResponse> completeFeature;

    public LiteHttpProxy(Consumer<LiteHttpProxy> recycleOption, boolean canBeReUse) {
        this.recycleOption = recycleOption;
        this.canBeReUse = canBeReUse;
    }

    public boolean canBePut() {
        return hasBeenPut == false;
    }

    public boolean canBeReuse() {
        return canBeReUse;
    }

    public void takeOption() {
        hasBeenPut = false;
    }

    public void putOption() {
        hasBeenPut = true;
    }

    public void release() {
        eventLoopGroup.shutdownGracefully();
        eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();

        if (recycleOption != null) {
            //todo 能被继续使用
            recycleOption.accept(this);
        }
    }


    public FullHttpResponse forward(HttpHostRoute httpHostRoute, FullHttpRequest fullHttpRequest) {
        LiteHttpProxy liteHttpProxy = this;
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();


                pipeline.addLast(new HttpClientCodec());
                pipeline.addLast(new HttpObjectAggregator(20 * 1024 * 1024));//限制缓冲最大值为2mb
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
            Channel channel = sync.channel();
            channel.writeAndFlush(fullHttpRequest);
            completeFeature = new BlockValueFeature<>();
            FullHttpResponse fullHttpResponse = completeFeature.get(10);
            return fullHttpResponse;
        } catch (Exception e) {
            throw new RuntimeException("目标uri" + fullHttpRequest.uri() + "使用规则" + httpHostRoute + "过程发生转发请求异常：" + e);
        } finally {
            release();
        }


    }


    public void writeData(FullHttpResponse data) {
        //todo 完成阻塞请求
        this.completeFeature.complete(data);
    }


}
