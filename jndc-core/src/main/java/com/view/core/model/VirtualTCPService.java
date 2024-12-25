package com.view.core.model;

import com.view.core.client.ControllableClient;
import com.view.core.client.tcp.TCPClient;
import com.view.core.client.tcp.TCPClientConfiguration;
import com.view.core.component.SupportEnvironment;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.utils.UniqueId;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Data
public class VirtualTCPService implements Serializable {
    private static final long serialVersionUID = -5630844448490385143L;

    //由上下文填写
    private String ndcClientId;

    //不序列化
    private transient SupportEnvironment supportEnvironment;

    /*------本地服务------*/
    private int expectPort;

    private String serviceId;

    private String description;

    private String host;

    private int port;

    //key:远程会话id
    private Map<String, TCPClient> controllableClientMap = new ConcurrentHashMap<>();
    /*------本地服务------*/


    /**
     * 为远程会话创建客户端
     *
     * @param tcpDataTransport 远程会话信息
     */
    public void openLocalServiceClient(TCPDataTransport tcpDataTransport, Consumer<TCPClient> clientStartedCallback) {
        String appServerSessionId = tcpDataTransport.getAppServerSessionId();
        String appServerId = tcpDataTransport.getAppServerId();


        TCPClientConfiguration tcpClientConfiguration = new TCPClientConfiguration();
        //读取数据回调
        tcpClientConfiguration.setReadCallBack(x -> {
            //todo 接收到数据

            byte[] data = x.getData();
            InetSocketAddress socketAddress = x.getRemote();


            TCPDataTransport dataTransport = new TCPDataTransport();

            //服务端信息

            dataTransport.setAppServerId(appServerId);
            dataTransport.setAppServerSessionId(appServerSessionId);

            //客户端信息
            dataTransport.setClientServiceId(serviceId);
            dataTransport.setClientServiceSessionId(UniqueId.generate());
            dataTransport.setData(data);


            NDCPacket ndcPacket = NDCPacketBuilder.dataPacket(tcpDataTransport);
            ndcPacket.setLocalAddress(socketAddress.getAddress());
            ndcPacket.setLocalPort(socketAddress.getPort());


            //向通道发送数据包
            if (supportEnvironment.NDC_CLIENT != null) {
                supportEnvironment.NDC_CLIENT.writePackage(ndcPacket);
            } else {
                log.warn("NDC_CLIENT未初始化");
            }
        });

        //启动成功回调
        tcpClientConfiguration.setStartSuccessCallBack((tcpClient) -> {

            //todo 回调

            //注册客户端
            controllableClientMap.put(appServerSessionId, tcpClient);
            clientStartedCallback.accept(tcpClient);

        });

        //todo 创建客户端
        TCPClient tcpClient = new TCPClient();
        tcpClient.setAppServerId(appServerId);
        tcpClient.setAppServerSessionId(appServerSessionId);
        tcpClient.setClientServiceId(serviceId);
        tcpClient.start(tcpClientConfiguration);
    }

    /**
     * 关闭服务客户端
     *
     * @param tcpDataTransport 远程会话信息
     */
    public void stopServiceClient(TCPDataTransport tcpDataTransport) {
        String appServerSessionId = tcpDataTransport.getAppServerSessionId();
        TCPClient tcpClient = controllableClientMap.get(appServerSessionId);
        if (tcpClient == null) {
            log.warn("未找到待关闭客户端", appServerSessionId);
        } else {
            tcpClient.stop();
            controllableClientMap.remove(appServerSessionId);
        }
    }

    /**
     * 接收远程会话数据
     *
     * @param tcpDataTransport 远程会话数据
     */
    public void receiveDataFromRemote(TCPDataTransport tcpDataTransport) {
        String appServerSessionId = tcpDataTransport.getAppServerSessionId();
        ControllableClient controllableClient = controllableClientMap.get(appServerSessionId);
        if (controllableClient == null) {
            //todo 接收数据
            log.warn("未找到客户端，数据丢弃：{}", appServerSessionId);
        } else {
            controllableClient.sendData(tcpDataTransport.getData());
        }

    }

    public String prettyDescription() {
        return ndcClientId + ":" + description + "(" + host + ":" + port + "--->sever:" + expectPort + ")";
    }

}
