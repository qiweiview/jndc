package jndc_server.core;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jndc.core.UniqueBeanManage;
import jndc.utils.LogPrint;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;


@ChannelHandler.Sharable
public class IPFilter extends ChannelInboundHandlerAdapter {
    public static String NAME = "IP_FILTER";
    public static final IPFilter STATIC_INSTANCE=new IPFilter();

    private volatile IpChecker ipChecker;

    public IPFilter() {
        LogPrint.debug("create filter object");
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetAddress address = socketAddress.getAddress();
        byte[] address1 = address.getAddress();
        if (address1.length > 4) {
            LogPrint.debug("unSupport ipv6 address:" + Arrays.toString(address1));
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListeners(ChannelFutureListener.CLOSE);
            return;
        }
        String ipString = address.getHostAddress();
        if (ipChecker ==null){
            ipChecker = UniqueBeanManage.getBean(IpChecker.class);
        }
        if (ipChecker.checkIpAddress(ipString)) {
            ctx.fireChannelActive();
        } else {
            LogPrint.info("block the ip" + Arrays.toString(address1));
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListeners(ChannelFutureListener.CLOSE);
            return;
        }


    }


}
