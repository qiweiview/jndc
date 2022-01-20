package jndc_server.core;

import jndc.core.NDCMessageProtocol;
import lombok.Data;

/**
 * 端口绑定上下文
 */
@Data
public class ServerPortBindContext {
    private int port;

    //0 物理端口 1 虚拟端口
    private int virtualTag;

    //端口监听对象，接收端口所有tcp请求（对外）
    private ServerPortProtector serverPortProtector;

    //端口绑定服务描述（对内）
    private TcpServiceDescriptionOnServer tcpServiceDescriptionOnServer;


    public ServerPortBindContext(int port) {
        this.port = port;
    }

    public void releaseRelatedResources() {
        //判断是否为物理端口
        if (isPhysics()) {
            //todo 释放端口监听器
            serverPortProtector.releaseRelatedResources();
        }
    }

    public boolean isPhysics() {
        return getVirtualTag() == 0;
    }


    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol) {
        serverPortProtector.receiveMessage(ndcMessageProtocol);
    }

    public void connectionInterrupt(NDCMessageProtocol ndcMessageProtocol) {
        serverPortProtector.connectionInterrupt(ndcMessageProtocol);
    }
}
