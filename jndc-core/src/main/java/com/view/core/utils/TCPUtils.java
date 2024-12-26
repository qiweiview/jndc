package com.view.core.utils;

import java.net.InetAddress;
import java.net.ServerSocket;

public class TCPUtils {

    public static boolean portBindable(int port) {
        //todo 检查本地端口是否被占用
        try (ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName("0.0.0.0"))) {
            //todo 端口可绑定
            return true;
        } catch (Exception e) {
            //todo 端口不可绑定
            return false;
        }
    }
}
