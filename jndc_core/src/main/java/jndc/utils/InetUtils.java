package jndc.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class InetUtils {

    public static InetAddress localInetAddress;

    public static String uniqueInetTag;

    static {
        loadMacAddress();
        loadLocalInetAddress();
    }



    public static InetAddress getInetAddressByHost(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("can not know the host: " + host);
        }
    }

    public static InetAddress getByStringIpAddress(String ipAddress) {
        InetAddress byAddress = null;
        try {
            byte[] bytes = parseIpv4Literal(ipAddress);
            if (bytes == null) {
                byAddress = getInetAddressByHost(ipAddress);
            } else {
                byAddress = InetAddress.getByAddress(bytes);
            }
        } catch (Exception e) {
            log.error("un know host :" + ipAddress);
            ApplicationExit.exit();
        }
        return byAddress;
    }

    private static byte[] parseIpv4Literal(String ipAddress) {
        if (ipAddress == null || "".equals(ipAddress)) {
            return null;
        }
        String[] parts = ipAddress.split("\\.", -1);
        if (parts.length != 4) {
            return null;
        }
        byte[] address = new byte[4];
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.length() == 0 || part.length() > 3) {
                return null;
            }
            int value = 0;
            for (int j = 0; j < part.length(); j++) {
                char ch = part.charAt(j);
                if (ch < '0' || ch > '9') {
                    return null;
                }
                value = value * 10 + (ch - '0');
            }
            if (value > 255) {
                return null;
            }
            address[i] = (byte) value;
        }
        return address;
    }


    /**
     * get local address
     */
    private static void loadLocalInetAddress() {
        try {
            localInetAddress = InetAddress.getByName("0.0.0.0");
        } catch (UnknownHostException e) {
            log.error("get local adress error");
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
