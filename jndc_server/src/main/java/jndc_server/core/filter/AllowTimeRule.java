package jndc_server.core.filter;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.UniqueBeanManage;
import jndc_server.core.JNDCServerConfig;
import jndc_server.core.NDCServerConfigCenter;
import jndc_server.core.ServerPortBindContext;
import jndc_server.core.ServerPortProtector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;

public class AllowTimeRule implements CustomRule {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile NDCServerConfigCenter ndcServerConfigCenter;

    private volatile int ignorePort;

    @Override
    public boolean ruleCheck(ChannelHandlerContext ctx) {
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
        if (port==ignorePort){
            return true;
        }

        Map<Integer, ServerPortBindContext> tcpRouter = ndcServerConfigCenter.getTcpRouter();
        ServerPortBindContext serverPortBindContext = tcpRouter.get(port);
        ServerPortProtector serverPortProtector = serverPortBindContext.getServerPortProtector();
        boolean b = serverPortProtector.checkBetweenEnableTimeRange();
        if (!b){
            logger.error("reject request from "+remoteAddress+" ,because out of service available time");
        }
        return b;
    }

    @Override
    public String getRuleName() {
        return "Allow Time Rule";
    }
}
