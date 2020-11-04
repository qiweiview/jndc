package jndc.core;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import jndc.utils.LogPrint;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;


@ChannelHandler.Sharable
public class IPFilter extends ChannelInboundHandlerAdapter {
    public static String NAME = "IP_FILTER";
    public static final IPFilter STATIC_INSTANCE=new IPFilter();


    public IPFilter() {
        LogPrint.info("create filter object");
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetAddress address = socketAddress.getAddress();
        byte[] address1 = address.getAddress();
        if (address1.length > 4) {
            LogPrint.err("unSupport ipv6 address:" + Arrays.toString(address1));
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListeners(ChannelFutureListener.CLOSE);
            return;
        }
        String ipString = address.getHostAddress();
        IpListChecker ipListChecker = UniqueBeanManage.getBean(IpListChecker.class);
        if (ipListChecker.checkIpAddress(ipString)) {
            ctx.fireChannelActive();
        } else {
            LogPrint.debug("block the ip" + Arrays.toString(address1));
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListeners(ChannelFutureListener.CLOSE);
            return;
        }


    }


}
