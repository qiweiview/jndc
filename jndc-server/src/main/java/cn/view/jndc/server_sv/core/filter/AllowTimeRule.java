package cn.view.jndc.server_sv.core.filter;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.UniqueBeanManage;
import jndc_server.config.JNDCServerConfig;
import jndc_server.core.AsynchronousEventCenter;
import jndc_server.core.NDCServerConfigCenter;
import jndc_server.core.port_app.ServerPortProtector;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;

@Slf4j
public class AllowTimeRule implements CustomRule {

    private volatile NDCServerConfigCenter ndcServerConfigCenter;

    private volatile int ignorePort;

    @Override
    public String ruleCheck(ChannelHandlerContext ctx) {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
        int port = socketAddress.getPort();
        if (ndcServerConfigCenter == null) {
            synchronized (AllowTimeRule.class) {
                if (ndcServerConfigCenter == null) {
                    ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                    ignorePort = UniqueBeanManage.getBean(JNDCServerConfig.class).getServicePort();
                }
            }
        }

        if (port == ignorePort) {
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
