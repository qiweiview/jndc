package jndc.core.config;

import jndc.utils.ApplicationExit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientPortMapping  implements ParameterVerification {
    private   final Logger logger = LoggerFactory.getLogger(getClass());

    private String name;

    private String localIp;

    private int localPort;

    private int serverPort;

    private boolean configEnable;

    private InetAddress localInetAddress;

    private InetSocketAddress inetSocketAddress;

    @Override
    public void performParameterVerification() {
        try {
            localInetAddress = InetAddress.getByName(localIp);
            inetSocketAddress=new InetSocketAddress(localInetAddress,localPort);
        } catch (Exception e) {
            logger.error("un know host :"+localIp);
            ApplicationExit.exit();
        }
    }

    @Override
    public String toString() {
        return "ClientPortMapping{" +
                "name='" + name + '\'' +
                ", localIp='" + localIp + '\'' +
                ", localPort=" + localPort +
                ", serverPort=" + serverPort +
                ", configEnable=" + configEnable +
                '}';
    }


    public InetAddress getLocalInetAddress() {
        return localInetAddress;
    }

    public void setLocalInetAddress(InetAddress localInetAddress) {
        this.localInetAddress = localInetAddress;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean getConfigEnable() {
        return configEnable;
    }

    public void setConfigEnable(boolean configEnable) {
        this.configEnable = configEnable;
    }


}
