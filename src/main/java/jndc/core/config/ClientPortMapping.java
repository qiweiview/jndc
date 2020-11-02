package jndc.core.config;

public class ClientPortMapping {
    private String name;

    private int localPort;

    private int serverPort;

    private boolean configEnable;

    @Override
    public String toString() {
        return "ClientPortMapping{" +
                "name='" + name + '\'' +
                ", localPort=" + localPort +
                ", remotePort=" + serverPort +
                ", configEnable=" + configEnable +
                '}';
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
