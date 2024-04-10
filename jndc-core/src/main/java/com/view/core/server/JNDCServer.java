package com.view.core.server;

import com.view.core.utils.SSLContextGenerator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JNDCServer {


    public void start(int port) {
        //http2服务器
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 生成自签名证书
            SslContext sslContext = SSLContextGenerator.generateSslContextAuto();

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();


                            //添加ssl支持
                            pipeline.addLast("ssl", sslContext.newHandler(ch.alloc()));

                            // 添加HttpServerCodec用于处理HTTP请求
                            pipeline.addLast("codec", new HttpServerCodec());

                            // 添加HttpObjectAggregator，将HTTP消息的多个部分聚合成完整的HTTP消息
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));


                            pipeline.addLast(new CustomerHttpHandler());

                        }
                    });

            log.info("起动服务https://127.0.0.1:" + port);
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
