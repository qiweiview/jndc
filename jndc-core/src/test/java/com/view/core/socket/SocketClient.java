package com.view.core.socket;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

@Slf4j
public class SocketClient {


    @Test
    public void simulateHttpServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(80);
        while (true) {
            try {
                Socket accept = serverSocket.accept();
                //读取报文
                InputStream inputStream = accept.getInputStream();
                byte[] bytes = new byte[1024];
                int read = inputStream.read(bytes);
                log.info("接收到数据：\n{}", new String(bytes, 0, read));
                log.info("=========");
                OutputStream outputStream = accept.getOutputStream();
                outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());
                outputStream.write("Content-Type: text/html; charset=utf-8\r\n".getBytes());
                outputStream.write("\r\n".getBytes());
                outputStream.write("<h1>hello im server</h1>".getBytes());
                outputStream.flush();
            } catch (IOException e) {
                log.error("接收连接异常", e);
            }
        }
    }

    @Test
    public void sendHttpBySocket() throws IOException {
        //使用Socket发送请求

        Socket socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress("qw607.com", 80);
        socket.connect(socketAddress);
        OutputStream outputStream = socket.getOutputStream();
        String data = "GET / HTTP/1.1\n" +
                "User-Agent: PostmanRuntime/7.42.0\n" +
                "Accept: */*\n" +
                "Postman-Token: ce64c633-2817-44cc-98e5-6079c1193a1d\n" +
                "Host: 127.0.0.1\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "\n" +
                "\n";
        outputStream.write(data.getBytes());
        try (InputStream inputStream = socket.getInputStream()) {
            byte[] bytes = new byte[1024];
            int read = inputStream.read(bytes);
            log.info("接收到数据：\n{}", new String(bytes, 0, read));
        }


    }
}