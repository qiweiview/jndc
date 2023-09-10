package com.view.jndc.core.v2.componet.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
public class ClientTCPDataHandle extends ChannelInboundHandlerAdapter {

    public static final String NAME = "ClientTCPDataHandle";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }


    /**
     * 服务由本地中断
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {


    }


}
