package cn.view.jndc.server_sv.core.filter;

import cn.view.jndc.server_sv.config.ApplicationReadyEventListener;
import cn.view.jndc.server_sv.core.AsynchronousEventCenter;
import cn.view.jndc.server_sv.core.NDCServerConfigCenter;
import cn.view.jndc.server_sv.core.port_app.ServerPortProtector;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllowTimeRule implements CustomRule {


    private final NDCServerConfigCenter ndcServerConfigCenter;


    @Override
    public String ruleCheck(ChannelHandlerContext ctx) {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
        int port = socketAddress.getPort();

        if (port == ApplicationReadyEventListener.RUNNING_PORT) {
            //todo 通过
            return null;
        }

        Map<Integer, AsynchronousEventCenter.ServerPortBindContext> tcpRouter = ndcServerConfigCenter.getTcpRouter();
        AsynchronousEventCenter.ServerPortBindContext serverPortBindContext = tcpRouter.get(port);
        ServerPortProtector serverPortProtector = serverPortBindContext.getServerPortProtector();
        //确认时间
        boolean b = serverPortProtector.checkBetweenEnableTimeRange();
        if (!b) {
            //todo false
            log.error("reject request from " + remoteAddress + " ,because out of service available time");
            return getRuleName() + "端口非可用时段";
        }
        return null;
    }

    @Override
    public String getRuleName() {
        return "Allow Time Rule";
    }
}
