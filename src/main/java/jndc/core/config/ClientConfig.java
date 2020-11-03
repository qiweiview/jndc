package jndc.core.config;

import jndc.utils.ApplicationExit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

public class ClientConfig implements ParameterVerification {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String remoteIp;

    private int remoteAdminPort;

    private List<ClientPortMapping> clientPortMappingList;

    private Map<Integer, InetSocketAddress> clientPortMappingMap;

    private InetAddress remoteInetAddress;

    private InetSocketAddress inetSocketAddress;


    @Override
    public void performParameterVerification() {
        try {
            remoteInetAddress = InetAddress.getByName(remoteIp);
            inetSocketAddress = new InetSocketAddress(remoteInetAddress, remoteAdminPort);
        } catch (Exception e) {
            logger.error("un know host ::" + remoteIp);
            ApplicationExit.exit();
        }

        if (clientPortMappingList != null) {

            clientPortMappingMap = new HashMap<>();
            Set<Integer> serverPortSet = new HashSet<>();
            clientPortMappingList.forEach(x -> {
                if (x.getConfigEnable()) {

                    if (serverPortSet.contains(x.getServerPort())) {
                        logger.error("duplicate remote port:" + x.getServerPort());
                        ApplicationExit.exit();
                    }
                    if (clientPortMappingMap.containsKey(x.getLocalPort())) {
                        logger.error("duplicate local port:" + x.getServerPort());
                        ApplicationExit.exit();
                    }
                    x.performParameterVerification();
                    clientPortMappingMap.put(x.getLocalPort(), x.getInetSocketAddress());
                    serverPortSet.add(x.getServerPort());
                }
            });
        }

    }


    @Override
    public String toString() {
        return "ClientConfig{" +
                "remoteIp='" + remoteIp + '\'' +
                ", remoteAdminPort='" + remoteAdminPort + '\'' +
                ", clientPortMappingList=" + clientPortMappingList +
                '}';
    }

    public Map<Integer, InetSocketAddress> getClientPortMappingMap() {
        return clientPortMappingMap;
    }

    public void setClientPortMappingMap(Map<Integer, InetSocketAddress> clientPortMappingMap) {
        this.clientPortMappingMap = clientPortMappingMap;
    }

    public InetAddress getRemoteInetAddress() {
        return remoteInetAddress;
    }

    public void setRemoteInetAddress(InetAddress remoteInetAddress) {
        this.remoteInetAddress = remoteInetAddress;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
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
