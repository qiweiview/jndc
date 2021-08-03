package jndc.http_support;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import jndc.utils.PackageScan;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Stream;

/**
 * jndc server core functions
 */
@Slf4j
public class NettyHttpServer {

    private MappingRegisterCenter mappingRegisterCenter = new MappingRegisterCenter();

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private String[] mappingSanPath;

    /**
     * 扫描映射
     */
    private void autoScanMapping() {
        if (mappingSanPath != null) {
            Stream.of(mappingSanPath).flatMap(x -> {
                List<Class> classes = PackageScan.scanClass(x);
                return classes.stream();
            }).forEach(x -> {
                mappingRegisterCenter.registerMapping(x);
            });
        }
    }

    public void start(int port) {

        autoScanMapping();

        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                String http = "http";//HttpServerCodec
                String oag = "oag";//HttpObjectAggregator


                pipeline.addLast(http, new HttpServerCodec());
                pipeline.addAfter(http, oag, new HttpObjectAggregator(2 * 1024 * 1024));//限制缓冲最大值为2mb
                HttpRequestHandle httpRequestHandle = new HttpRequestHandle();
                httpRequestHandle.setMappingRegisterCenter(mappingRegisterCenter);
                pipeline.addAfter(oag, HttpRequestHandle.NAME, httpRequestHandle);
            }
        };

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)//
                .localAddress(port)//　
                .childHandler(channelInitializer);

        b.bind().addListener(x -> {
            if (x.isSuccess()) {
                log.info("bind http  : " + port + " success");
            } else {
                log.error("bind http : " + port + " fail,cause " + x);
            }

        });


    }


    public void setMappingSanPath(String... scanPath) {
        mappingSanPath = scanPath;
    }


}
