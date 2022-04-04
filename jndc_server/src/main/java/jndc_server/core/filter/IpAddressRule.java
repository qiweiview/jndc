package jndc_server.core.filter;


import io.netty.channel.ChannelHandlerContext;
import jndc.core.UniqueBeanManage;
import jndc_server.core.IpChecker;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

@Slf4j
public class IpAddressRule implements CustomRule {
    private volatile IpChecker ipChecker;


    @Override
    public String ruleCheck(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetAddress address = socketAddress.getAddress();
        byte[] address1 = address.getAddress();
        if (address1.length > 4) {
            log.debug("仅支持ipv4地址，当前地址：" + Arrays.toString(address1));
            return getRuleName() + "仅支持ipv4地址，当前地址：" + Arrays.toString(address1);
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

        boolean b = ipChecker.checkIpAddress(ipString);
        if (b) {
            return null;
        } else {
            return getRuleName() + "发起端地址不被允许：" + ipString;
        }

    }

    @Override
    public String getRuleName() {
        return "Ip Address Rule";
    }
}
