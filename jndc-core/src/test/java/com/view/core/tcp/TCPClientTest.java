package com.view.core.tcp;

import com.view.core.client.tcp.TCPClient;
import com.view.core.component.SupportEnvironment;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.VirtualTCPService;
import com.view.core.utils.UniqueId;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class TCPClientTest {

    private TCPClient client;

    private SupportEnvironment supportEnvironment;

    @BeforeEach
    public void init() {
          supportEnvironment = new SupportEnvironment();
        client = new TCPClient(supportEnvironment);
    }

    @Test
    public void runClient() {
        AtomicBoolean ready = new AtomicBoolean(false);
        new Thread(() -> {
            while (!ready.get()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            client.receiveData("hello im client".getBytes());
        }).start();

        client.start("127.0.0.1", 888, () -> {
            log.info("客户端启动成功");
            ready.set(true);
        });

    }


    @Test
    public void simulateActiveAndDataSend() {

        VirtualTCPService virtualTCPService = new VirtualTCPService();
        String generate = UniqueId.generate();
        log.info("生成的服务id为：{}", generate);
        virtualTCPService.setServiceId(generate);
        virtualTCPService.setDescription("测试服务");
        virtualTCPService.setHost("qw607.com");
        virtualTCPService.setPort(80);
        virtualTCPService.setExpectPort(3307);

        TCPDataTransport tcpDataTransport = new TCPDataTransport();
        tcpDataTransport.setAppServerId("appServerId");
        tcpDataTransport.setAppServerSessionId("appServerSessionId");


        Runnable callBack = () -> {
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
            tcpDataTransport.setData(data.getBytes());

            //接收消息
            virtualTCPService.receiveDataFromRemote(tcpDataTransport);
        };


        //触发active
        virtualTCPService.openLocalServiceClient(tcpDataTransport, tcpClient -> {
            //todo 连接创建后的回调


            //绑定插槽
            tcpClient.addSlot((data) -> {
                log.info("收到数据：\n{}", new String(data));
            });

            //异步发送数据
            supportEnvironment.EVENT_BUS.post(callBack);
        });


    }
}
