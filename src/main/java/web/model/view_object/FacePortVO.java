package web.model.view_object;

import jndc.core.NDCMessageProtocol;

import java.net.InetAddress;

public class FacePortVO {
    private int serverPort;
    private int localPort;
    private String localIp;

    public static FacePortVO of(NDCMessageProtocol registerMessage) {
        FacePortVO facePortVO = new FacePortVO();
        facePortVO.setServerPort(registerMessage.getServerPort());
        facePortVO.setLocalPort(registerMessage.getLocalPort());
        InetAddress localInetAddress = registerMessage.getLocalInetAddress();
        String hostAddress = localInetAddress.getHostAddress();
        facePortVO.setLocalIp(hostAddress);
        return facePortVO;

    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }
}
