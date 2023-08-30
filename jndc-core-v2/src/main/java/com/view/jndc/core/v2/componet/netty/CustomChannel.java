package com.view.jndc.core.v2.componet.netty;

import com.view.jndc.core.v2.componet.netty.codec.EncodedCodec;
import com.view.jndc.core.v2.componet.netty.codec.ObjectCodec;
import com.view.jndc.core.v2.componet.netty.handler.ServerMessageHandler;
import com.view.jndc.core.v2.model.jndc.JNDCData;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomChannel extends ChannelInitializer<Channel> {
    private ServerMessageHandler serverMessageHandler;

    private volatile boolean loadingFinished = false;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline entries = ch.pipeline();
        entries.addFirst(EncodedCodec.NAME, new EncodedCodec());
        entries.addAfter(EncodedCodec.NAME, ObjectCodec.NAME, new ObjectCodec());
        serverMessageHandler = new ServerMessageHandler();
        entries.addAfter(ObjectCodec.NAME, ServerMessageHandler.NAME, serverMessageHandler);

        loadingFinished = true;

        synchronized (this) {
            notifyAll();
        }
    }

    public void write(JNDCData jndcData) {
        if (!loadingFinished) {

            Thread currentThread = Thread.currentThread();
            synchronized (this) {
                try {
                    wait();

                } catch (InterruptedException e) {
                    log.error("等待中断", e);
                }
            }


        }

        serverMessageHandler.write(jndcData);
    }
}
