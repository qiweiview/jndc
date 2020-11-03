package jndc.core.config;

public class ClientPortMapping {
    private String name;

    private String localIp;

    private int localPort;

    private int serverPort;

    private boolean configEnable;

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

    public boolean isConfigEnable() {
        return configEnable;
    }

    public void setConfigEnable(boolean configEnable) {
        this.configEnable = configEnable;
    }
}
