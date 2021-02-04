package jndc_server.core.filter;

import io.netty.channel.ChannelHandlerContext;

public interface CustomRule {

    public boolean ruleCheck(ChannelHandlerContext context);


    public String getRuleName();
}
