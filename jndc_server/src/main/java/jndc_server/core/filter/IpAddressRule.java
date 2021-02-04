package jndc_server.core.filter;


import io.netty.channel.ChannelHandlerContext;
import jndc.core.UniqueBeanManage;
import jndc.utils.LogPrint;
import jndc_server.core.IpChecker;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class IpAddressRule implements CustomRule {
    private volatile IpChecker ipChecker;


    @Override
    public boolean ruleCheck(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetAddress address = socketAddress.getAddress();
        byte[] address1 = address.getAddress();
        if (address1.length > 4) {
            LogPrint.debug("unSupport ipv6 address:" + Arrays.toString(address1));
            return false;
        }
        String ipString = address.getHostAddress();

        //lazy init
        if (ipChecker == null) {
            synchronized (IpAddressRule.class) {
                if (ipChecker == null) {
                    ipChecker = UniqueBeanManage.getBean(IpChecker.class);
                }
            }
        }

        return ipChecker.checkIpAddress(ipString);
    }

    @Override
    public String getRuleName() {
        return "Ip Address Rule";
    }
}
