package jndc.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class InetUtils {
    public static InetAddress localInetAddress;

    public static InetSocketAddress localTestInetScoket;

    public static String uniqueInetTag;

    static {
        loadLocalInetAddress();
        loadMacAddress();


    }


    public static InetSocketAddress getLocalInetAddress(int port) {
        try {
            InetAddress localhost = InetAddress.getByName("127.0.0.1");
          return new InetSocketAddress(localhost, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadLocalInetAddress() {
        try {
            InetAddress localhost = InetAddress.getByName("127.0.0.1");
            localTestInetScoket = new InetSocketAddress(localhost, 80);
            localInetAddress=localTestInetScoket.getAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);

        }
    }

    public static void loadMacAddress() {
        try {
            uniqueInetTag = GetNetworkAddress.GetAddress("ip") + "/" + GetNetworkAddress.GetAddress("mac");
        } catch (Exception e) {
            uniqueInetTag = "0.0.0.0/" + UUIDSimple.id();

        }
    }


}
