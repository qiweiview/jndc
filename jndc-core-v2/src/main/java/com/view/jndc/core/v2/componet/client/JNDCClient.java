package com.view.jndc.core.v2.componet.client;

import com.view.jndc.core.v2.componet.SpaceManager;
import com.view.jndc.core.v2.componet.netty.CustomChannel;
import com.view.jndc.core.v2.enum_value.HandlerType;
import com.view.jndc.core.v2.model.jndc.JNDCData;
import com.view.jndc.core.v2.model.json_object.ChannelRegister;
import com.view.jndc.core.v2.utils.PathUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JNDCClient extends SpaceManager {

    private CustomChannel customChannel;

    private String host;

    private int port;

    public void start(String host, int port) {

        this.host = host;

        this.port = port;


        Bootstrap b = new Bootstrap();


        customChannel = new CustomChannel(HandlerType.CLIENT_HANDLER.value);


        b.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)//
                .option(ChannelOption.SO_KEEPALIVE, true)//tcp keep alive
                .handler(customChannel);

        ChannelFuture connect = b.connect(host, port);
        connect.addListeners(x -> {
            if (x.isSuccess()) {
                //todo 连接成功

                log.info("连接成功jndc://" + host + ":" + port);
            } else {
                //todo 连接失败

                log.info("连接失败jndc://" + host + ":" + port);
            }

        });
    }


    /**
     * 发送包
     *
     * @param jndcData
     */
    public void write(JNDCData jndcData) {
        customChannel.write(jndcData);
    }

    /**
     * 注册服务
     */
    public void registerService(String serviceName, String descHost, int descPort) {
    }

    /**
     * 打开隧道
     */
    public void openChannel() {
        //构造对象
        JNDCData jndcData = JNDCData.openChannel();
        ChannelRegister channelRegister = new ChannelRegister();
        String id = PathUtils.getRuntimeUniqueId(getConfig() + File.separator, PathUtils.INSTANCE_ID);
        channelRegister.setChannelId(id);
        jndcData.setData(channelRegister.serialize());
        write(jndcData);
    }

    public void testBandwidth(int i, TimeUnit seconds) {
        Thread thread = new Thread(() -> {
            Thread currentThread = Thread.currentThread();
            while (!currentThread.isInterrupted()) {
                write(JNDCData.testBandwidth());
            }
        });

        thread.start();
        try {
            seconds.sleep(i);
        } catch (InterruptedException e) {
            log.error("休眠失败");
        }

        //中断
        thread.interrupt();


    }
}
