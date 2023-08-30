package com.view.jndc.core.v2.componet.netty.codec;

import com.view.jndc.core.v2.model.jndc.JNDCData;
import com.view.jndc.core.v2.model.protocol_message.JNDCEncoded;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 接编码器
 */
@Slf4j
public class ObjectCodec extends MessageToMessageCodec<JNDCEncoded, JNDCData> {

    public static final String NAME = "ObjectCodec";


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, JNDCData jndcData, List<Object> list) throws Exception {
        JNDCEncoded jndcEncoded = jndcData.toEncoded();
        list.add(jndcEncoded);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, JNDCEncoded jndcEncoded, List<Object> list) throws Exception {
        JNDCData jndcData = JNDCData.parse(jndcEncoded);
        list.add(jndcData);

    }
}
