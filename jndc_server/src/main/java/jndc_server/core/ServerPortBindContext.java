package jndc_server.core;

import jndc.core.NDCMessageProtocol;
import jndc.core.TcpServiceDescription;

/**
 * port bind context
 */
public class ServerPortBindContext {
    private int port;

    private int virtualTag;//0 physics 1 virtual

    private ServerPortProtector serverPortProtector;//port protector

    private TcpServiceDescriptionOnServer tcpServiceDescription;//bind service description

    public void releaseRelatedResources() {
        if (isPhysics()) {
            serverPortProtector.releaseRelatedResources();
        }
    }

    public boolean isPhysics() {
        return getVirtualTag() == 0;
    }




    /* --------------------getter setter---------------- */

    public int getVirtualTag() {
        return virtualTag;
    }

    public void setVirtualTag(int virtualTag) {
        this.virtualTag = virtualTag;
    }

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

    public TcpServiceDescriptionOnServer getTcpServiceDescription() {
        return tcpServiceDescription;
    }

    public void setTcpServiceDescription(TcpServiceDescriptionOnServer tcpServiceDescription) {
        this.tcpServiceDescription = tcpServiceDescription;
    }

    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol) {
        serverPortProtector.receiveMessage(ndcMessageProtocol);
    }

    public void connectionInterrupt(NDCMessageProtocol ndcMessageProtocol) {
        serverPortProtector.connectionInterrupt(ndcMessageProtocol);
    }
}
