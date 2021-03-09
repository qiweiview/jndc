package jndc_server.web_support.http_module;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.ScheduledFuture;
import jndc_server.web_support.model.data_object.HttpHostRoute;

import java.util.concurrent.*;


public class LiteHttpProxy {
    private ChannelHandlerContext channelHandlerContext;
    private EventLoopGroup eventLoopGroup;
    private FullHttpRequest fullHttpRequest;
    private  HttpHostRoute httpHostRoute;
    private Thread holdThread;


    public LiteHttpProxy(ChannelHandlerContext channelHandlerContext, HttpHostRoute httpHostRoute,FullHttpRequest fullHttpRequest) {
        this.httpHostRoute=httpHostRoute;
        this.fullHttpRequest = fullHttpRequest;
        this.channelHandlerContext = channelHandlerContext;
        this.eventLoopGroup = channelHandlerContext.channel().eventLoop();
    }

    public void release() {
        channelHandlerContext = null;
        eventLoopGroup = null;
        fullHttpRequest = null;
        httpHostRoute = null;
    }

    public ScheduledFuture<FullHttpResponse> forward() {
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
            sync = b.connect(httpHostRoute.getForwardHost(), httpHostRoute.getForwardPort()).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Channel channel = sync.channel();
        channel.writeAndFlush(fullHttpRequest);
        Callable<FullHttpResponse> callable=()->{
            holdThread=Thread.currentThread();
            synchronized (holdThread){
                holdThread.wait();
            }
          return null;
        };
        ScheduledFuture<FullHttpResponse> schedule =channel.eventLoop().schedule(callable, 15, TimeUnit.SECONDS);
        return schedule;

    }


    public static void main(String[] args) {
        FutureTask futureTask = new FutureTask(() -> {
            System.out.println("task run");
            return 1;
        });
        try {
            Object o = futureTask.get();

            System.out.println("run finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
    public void writeData(FullHttpResponse fullHttpResponse){
        channelHandlerContext.writeAndFlush(fullHttpResponse);
    }


}
