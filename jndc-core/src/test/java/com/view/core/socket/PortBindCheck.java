package com.view.core.socket;

import com.view.core.component.app_center.AppCenter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class PortBindCheck {


    @Test
    public void run() {
        boolean b = AppCenter.portBindable(3306);
        log.info("端口是否可绑定：{}", b);
    }

    @Test
    public void mockServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        while (true) {
            try {
                Socket accept = serverSocket.accept();
                log.info("接收到连接：{}", accept);
                OutputStream outputStream = accept.getOutputStream();
                outputStream.write("hello im server".getBytes());
                outputStream.flush();


                InputStream inputStream = accept.getInputStream();
                byte[] bytes = new byte[1024];
                int read = inputStream.read(bytes);
                log.info("接收到数据：{}", new String(bytes, 0, read));
            } catch (IOException e) {
                log.error("接收连接异常", e);
            }
        }
    }
}
