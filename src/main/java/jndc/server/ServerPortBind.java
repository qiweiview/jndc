package jndc.server;

import java.net.InetSocketAddress;

public class ServerPortBind {

    private InetSocketAddress bindInetSocketAddress;//服务端的ip和端口

    private String clientId;

    private InetSocketAddress clientInetSocketAddress;//本地的ip和端口

    public InetSocketAddress getBindInetSocketAddress() {
        return bindInetSocketAddress;
    }

    public void setBindInetSocketAddress(InetSocketAddress bindInetSocketAddress) {
        this.bindInetSocketAddress = bindInetSocketAddress;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public InetSocketAddress getClientInetSocketAddress() {
        return clientInetSocketAddress;
    }

    public void setClientInetSocketAddress(InetSocketAddress clientInetSocketAddress) {
        this.clientInetSocketAddress = clientInetSocketAddress;
    }
}
