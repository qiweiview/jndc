package cn.view.jndc.server_sv.core.filter;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.core.NDCMessageProtocol;
import jndc.core.message.UserError;
import jndc.utils.ObjectSerializableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;


/**
 * 自定义规则过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomRulesFilter extends ChannelInboundHandlerAdapter {

    public static String NAME = "CUSTOM_RULES_FILTER";

    private final IpAddressRule ipAddressRule;

    private final AllowTimeRule allowTimeRule;



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String errMsg;
        errMsg = ipAddressRule.ruleCheck(ctx);
        errMsg = allowTimeRule.ruleCheck(ctx);

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

        //通过，进入下一环节
        ctx.fireChannelActive();
    }


}
