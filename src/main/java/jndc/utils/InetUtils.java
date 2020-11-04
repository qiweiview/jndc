package jndc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.util.IPAddressUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class InetUtils {
    private static final Logger logger = LoggerFactory.getLogger(InetUtils.class);

    public static InetAddress localInetAddress;

    public static String uniqueInetTag;

    static {
        loadMacAddress();
        loadLocalInetAddress();
    }


    public static InetAddress getByStringIpAddress(String ipAddress) {
        InetAddress byAddress = null;
        try {
            byte[] bytes = IPAddressUtil.textToNumericFormatV4(ipAddress);
            if (bytes == null) {
                logger.error("un support ip address:" + ipAddress);
                ApplicationExit.exit();
            }
            byAddress = InetAddress.getByAddress(bytes);
        } catch (Exception e) {
            logger.error("un know host :" + ipAddress);
            ApplicationExit.exit();
        }
        return byAddress;
    }


    /**
     * get local address
     */
    private static void loadLocalInetAddress() {
        try {
            localInetAddress = InetAddress.getByName("0.0.0.0");
        } catch (UnknownHostException e) {
            logger.error("get local adress error");
            ApplicationExit.exit();

        }
    }

    /**
     * get local mac address
     */
    private static void loadMacAddress() {
        try {
            uniqueInetTag = GetNetworkAddress.GetAddress("ip") + "/" + GetNetworkAddress.GetAddress("mac");
        } catch (Exception e) {
            uniqueInetTag = "0.0.0.0/" + UUIDSimple.id();
        }
    }


}
