package com.view.core.native_tcp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NativeTCPTest {
    private ExecutorService executorService = Executors.newCachedThreadPool();


    @Test
    public void runServer() throws IOException {

        ServerSocket serverSocket = new ServerSocket(18888);
        log.info("runServer：18888");
        while (true) {
            Socket accept = serverSocket.accept();
            executorService.submit(() -> {
                //循环读取数据直到读取内容包含1则断开连接
                try {
                    InputStream inputStream = accept.getInputStream();
                    OutputStream outputStream = accept.getOutputStream();
                    statisticalHandleServer(inputStream);
                    batchSend(outputStream);
                    accept.close();
                    log.info("关闭连接");
                } catch (Exception e) {
                    log.error("IO失败{}", e.getMessage());
                }
            });
        }
    }

    @Test
    public void runClient() throws Exception {
//       Socket socket=new Socket("127.0.0.1",18888);
        Socket socket = new Socket("121.4.103.198", 7777);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        //发送数据
        batchSend(outputStream);

        //接收数据
        statisticalHandleClient(inputStream);

        TimeUnit.SECONDS.sleep(15);
    }

    public void batchSend(OutputStream outputStream) throws IOException {
        int total = 1;
        //创建一个10mb都是0的数据
        for (int i = 0; i < 10; i++) {
            //分10次发
            byte[] bytes = new byte[1024 * 1024];
            outputStream.write(bytes);
            total += bytes.length;
        }

        outputStream.write(1);
        outputStream.flush();
        log.info("发送送完成{}", total);
    }

    private void statisticalHandleServer(InputStream inputStream) throws IOException {
        int total = 0;
        byte[] bytes = new byte[1024];
        boolean readable = true;
        while (readable) {
            int read = inputStream.read(bytes);
            log.debug("read once:{}", read);
            if (read == -1) {
                break;
            }
            byte[] readByte = Arrays.copyOf(bytes, read);
            total += readByte.length;
            //判断bytes中包含1
            for (byte aByte : readByte) {
                if (aByte == 1) {
                    log.info("接收:{}", total);
                    readable = false;
                    break;
                }
            }
        }
    }

    private void statisticalHandleClient(InputStream inputStream) throws IOException {
        int total = 0;
        byte[] bytes = new byte[1024];
        boolean readable = true;
        while (readable) {
            int read = inputStream.read(bytes);
            log.debug("read once:{}", read);
            if (read == -1) {
                break;
            }
            byte[] readByte = Arrays.copyOf(bytes, read);
            total += readByte.length;
            //判断bytes中包含1
            for (byte aByte : readByte) {
                if (aByte == 1) {
                    log.info("接收:{}", total);
                    readable = false;
                    break;
                }
            }
        }
    }


}
