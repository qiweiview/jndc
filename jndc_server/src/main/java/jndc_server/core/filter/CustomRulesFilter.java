package jndc_server.core.filter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Slf4j
@ChannelHandler.Sharable
public class CustomRulesFilter extends ChannelInboundHandlerAdapter {
    public static String NAME = "CUSTOM_RULES_FILTER";
    public static final CustomRulesFilter STATIC_INSTANCE = new CustomRulesFilter();

    private static List<CustomRule> customRuleList = new ArrayList<>();

    static {
        customRuleList.add(new IpAddressRule());
        customRuleList.add(new AllowTimeRule());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Iterator<CustomRule> iterator = customRuleList.iterator();
        while (iterator.hasNext()) {
            CustomRule next = iterator.next();
            if (!next.ruleCheck(ctx)) {
                //todo not pass
                log.debug("request be block because the " + next.getRuleName());
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListeners(ChannelFutureListener.CLOSE);
                return;
            }
        }

        //pass all rules
        ctx.fireChannelActive();
    }


}
