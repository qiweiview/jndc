package com.view.core.tcp;

import com.view.core.client.tcp.TCPClient;
import com.view.core.client.tcp.TCPClientConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class TCPClientTest {

    private TCPClient client;

    @BeforeEach
    public void init() {
        client = new TCPClient();
    }

    @Test
    public void runClient() {

        TCPClientConfiguration tcpClientConfiguration = new TCPClientConfiguration();
        tcpClientConfiguration.setHost("qw607.com");
        tcpClientConfiguration.setPort(80);

        tcpClientConfiguration.setReadCallBack(tcpData -> {
            log.info("接收到数据：{}", new String(tcpData.getData()));
        });

        tcpClientConfiguration.setReadCompleteCallBack((tcpDataTransport, client) -> {
            log.info("读取完成");
            client.stop();
        });

        tcpClientConfiguration.setStartSuccessCallBack(tcpClient -> {
            log.info("客户端启动成功");
        });

        tcpClientConfiguration.setStartFailCallBack(tcpClient -> {
            log.info("客户端启动失败");
        });

        tcpClientConfiguration.setActiveCallBack((tcpDataTransport, client) -> {
            log.info("连接成功");
            //发送消息
            String data = "GET / HTTP/1.1\n" +
                    "User-Agent: PostmanRuntime/7.42.0\n" +
                    "Accept: */*\n" +
                    "Postman-Token: ce64c633-2817-44cc-98e5-6079c1193a1d\n" +
                    "Host: 127.0.0.1\n" +
                    "Accept-Encoding: gzip, deflate, br\n" +
                    "Connection: keep-alive\n" +
                    "\n" +
                    "\n";

            client.sendData(data.getBytes());
        });

        tcpClientConfiguration.setInactiveCallBack((tcpDataTransport, client) -> {
            log.info("连接断开");
        });

        client.start(tcpClientConfiguration);

    }


}
