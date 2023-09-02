package com.view.jndc.core.v2.componet.netty;

import com.view.jndc.core.v2.componet.netty.codec.EncodedCodec;
import com.view.jndc.core.v2.componet.netty.codec.ObjectCodec;
import com.view.jndc.core.v2.componet.netty.handler.ClientMessageHandler;
import com.view.jndc.core.v2.componet.netty.handler.ServerMessageHandler;
import com.view.jndc.core.v2.componet.netty.handler.WriteableHandler;
import com.view.jndc.core.v2.enum_value.HandlerType;
import com.view.jndc.core.v2.exception.BixException;
import com.view.jndc.core.v2.model.jndc.JNDCData;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomChannel extends ChannelInitializer<Channel> {

    private String handlerType;

    private WriteableHandler writeableHandler;

    private volatile boolean loadingFinished = false;


    public CustomChannel(String handlerType) {
        this.handlerType = handlerType;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline entries = ch.pipeline();
        entries.addFirst(EncodedCodec.NAME, new EncodedCodec());
        entries.addAfter(EncodedCodec.NAME, ObjectCodec.NAME, new ObjectCodec());


        String nm;
        if (HandlerType.CLIENT_HANDLER.value.equals(handlerType)) {
            writeableHandler = new ClientMessageHandler();
            nm = ClientMessageHandler.NAME;
        } else if (HandlerType.SERVER_HANDLER.value.equals(handlerType)) {
            writeableHandler = new ServerMessageHandler();
            nm = ServerMessageHandler.NAME;
        } else {
            throw new BixException("未知的处理器类型：" + handlerType);
        }

        entries.addAfter(ObjectCodec.NAME, nm, writeableHandler);

        loadingFinished = true;

        synchronized (this) {
            notifyAll();
        }
    }

    public void write(JNDCData jndcData) {
        if (!loadingFinished) {
            synchronized (this) {
                try {
                    wait();

                } catch (InterruptedException e) {
                    log.error("等待中断", e);
                }
            }


        }

        writeableHandler.write(jndcData);
    }
}
