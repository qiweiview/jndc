package cn.view.jndc.server_sv.core.filter;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.core.NDCMessageProtocol;
import jndc.core.message.UserError;
import jndc.utils.ObjectSerializableUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 自定义规则过滤器
 */
@Slf4j
@ChannelHandler.Sharable
public class CustomRulesFilter extends ChannelInboundHandlerAdapter {

    public static String NAME = "CUSTOM_RULES_FILTER";

    public static final CustomRulesFilter STATIC_INSTANCE = new CustomRulesFilter();

    private static List<CustomRule> customRuleList = new ArrayList<>();

    static {
        //添加过滤器
        customRuleList.add(new IpAddressRule());
        customRuleList.add(new AllowTimeRule());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Iterator<CustomRule> iterator = customRuleList.iterator();
        while (iterator.hasNext()) {
            CustomRule next = iterator.next();
            String errMsg = next.ruleCheck(ctx);
            if (errMsg != null) {
                //todo not pass
                log.debug("拦截请求 :" + errMsg);

                UserError userError = new UserError();
                userError.setDescription(errMsg);
                byte[] bytes = ObjectSerializableUtils.object2bytes(userError);


                //发送异常信息
                InetAddress unused = InetAddress.getLocalHost();
                NDCMessageProtocol tqs = NDCMessageProtocol.of(unused, unused, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.USER_ERROR);
                tqs.setData(bytes);
                ctx.writeAndFlush(tqs).addListeners(ChannelFutureListener.CLOSE);
                return;
            }
        }

        //通过，进入下一环节
        ctx.fireChannelActive();
    }


}
