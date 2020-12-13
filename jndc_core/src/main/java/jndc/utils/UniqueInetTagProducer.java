package jndc.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UniqueInetTagProducer {


    public static String get4Server(InetAddress inetAddress, int port){
        byte[] address = inetAddress.getAddress();
        return new String(address)+port;
    }

    public static String get4Server(InetSocketAddress inetSocketAddress){
        if (null==inetSocketAddress ||inetSocketAddress.getAddress()==null ||inetSocketAddress.getAddress().getAddress()==null){
            throw new RuntimeException("not support for null");
        }
        int port = inetSocketAddress.getPort();

        byte[] address = inetSocketAddress.getAddress().getAddress();
        return new String(address)+port;

    }
    public static String get4Client(InetAddress inetAddress, int port){
        byte[] address = inetAddress.getAddress();
        return new String(address)+port;
    }

    public static String get4Client(InetSocketAddress sender){
        if (null==sender
                ||sender.getAddress()==null
                ||sender.getAddress().getAddress()==null){
            throw new RuntimeException("not support for null");
        }
        int port = sender.getPort();

        byte[] address = sender.getAddress().getAddress();
        return new String(address)+port;

    }
}
