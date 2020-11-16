package jndc.server;

import jndc.core.NDCMessageProtocol;
import jndc.core.TcpServiceDescription;

/**
 * port bind context
 */
public class ServerPortBindContext {
    private int port;

    private ServerPortProtector serverPortProtector;//port protector

    private TcpServiceDescription tcpServiceDescription;//bind service description

    public void releaseRelatedResources() {
        serverPortProtector.releaseRelatedResources();
    }


    /* --------------------getter setter---------------- */


    public ServerPortBindContext(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerPortProtector getServerPortProtector() {
        return serverPortProtector;
    }

    public void setServerPortProtector(ServerPortProtector serverPortProtector) {
        this.serverPortProtector = serverPortProtector;
    }

    public TcpServiceDescription getTcpServiceDescription() {
        return tcpServiceDescription;
    }

    public void setTcpServiceDescription(TcpServiceDescription tcpServiceDescription) {
        this.tcpServiceDescription = tcpServiceDescription;
    }

    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol) {
        serverPortProtector.receiveMessage(ndcMessageProtocol);
    }

    public void connectionInterrupt(NDCMessageProtocol ndcMessageProtocol) {
        serverPortProtector.connectionInterrupt(ndcMessageProtocol);
    }
}
