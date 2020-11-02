package jndc.core.config;

import java.util.List;

public class ClientConfig {
    private String remoteIp;

    private int remoteAdminPort;

    private List<ClientPortMapping> clientPortMappingList;


    @Override
    public String toString() {
        return "ClientConfig{" +
                "remoteIp='" + remoteIp + '\'' +
                ", remoteAdminPort='" + remoteAdminPort + '\'' +
                ", clientPortMappingList=" + clientPortMappingList +
                '}';
    }

    public List<ClientPortMapping> getClientPortMappingList() {
        return clientPortMappingList;
    }


    public void setClientPortMappingList(List<ClientPortMapping> clientPortMappingList) {
        this.clientPortMappingList = clientPortMappingList;
    }


    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public int getRemoteAdminPort() {
        return remoteAdminPort;
    }

    public void setRemoteAdminPort(int remoteAdminPort) {
        this.remoteAdminPort = remoteAdminPort;
    }
}
