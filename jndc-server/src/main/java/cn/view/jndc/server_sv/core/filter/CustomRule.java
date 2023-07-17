package cn.view.jndc.server_sv.core.filter;

import io.netty.channel.ChannelHandlerContext;

public interface CustomRule {

    /**
     * 规则确认
     *
     * @param context
     * @return
     */
    public String ruleCheck(ChannelHandlerContext context);


    public String getRuleName();
}
