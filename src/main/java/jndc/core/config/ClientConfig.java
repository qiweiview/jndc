package jndc.core.config;

import jndc.utils.LogPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ClientConfig {
    private   final Logger logger = LoggerFactory.getLogger(getClass());

    private String remoteIp;

    private int remoteAdminPort;

    private List<ClientPortMapping> clientPortMappingList;

    private InetAddress  remoteInetAddress;

    public InetAddress getRemoteInetAddress(){
        if (remoteInetAddress==null){
            try {
                remoteInetAddress=InetAddress.getByName(remoteIp);
            } catch (UnknownHostException e) {
                logger.debug("unknown  remote host");
                System.exit(1);
            }
        }
        return remoteInetAddress;
    }


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
