package com.view.jndc.core.v2.utils;

import java.net.InetSocketAddress;
import java.util.UUID;

public class UniqueIdExtractor {

    public static String generate() {
        return UUID.randomUUID().toString();
    }


    public static String get4Server(InetSocketAddress inetSocketAddress) {
        if (null == inetSocketAddress) {
            throw new RuntimeException("not support for null");
        }

        return inetSocketAddress.toString();

    }
}
