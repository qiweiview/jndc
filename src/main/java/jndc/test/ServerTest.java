package jndc.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jndc.core.NDCPCodec;
import jndc.core.NettyComponentConfig;
import jndc.server.JNDCServer;
import jndc.server.JNDCServerMessageHandle;
import jndc.utils.LogPrint;

import java.net.InetSocketAddress;

public class ServerTest {
    public static final Integer SERVER_PORT=81;



    public static void main(String[] args) {
        JNDCServer serverTest =new JNDCServer(SERVER_PORT);
        serverTest.createServer();
    }


}
