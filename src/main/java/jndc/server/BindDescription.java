package jndc.server;

import java.net.InetSocketAddress;

public class BindDescription {
    private InetSocketAddress serverInetSocket;
    private InetSocketAddress clientInetSocket;


    public BindDescription(InetSocketAddress serverInetSocket, InetSocketAddress clientInetSocket) {
        this.serverInetSocket = serverInetSocket;
        this.clientInetSocket = clientInetSocket;
    }

    public InetSocketAddress getServerInetSocket() {
        return serverInetSocket;
    }

    public void setServerInetSocket(InetSocketAddress serverInetSocket) {
        this.serverInetSocket = serverInetSocket;
    }

    public InetSocketAddress getClientInetSocket() {
        return clientInetSocket;
    }

    public void setClientInetSocket(InetSocketAddress clientInetSocket) {
        this.clientInetSocket = clientInetSocket;
    }
}
