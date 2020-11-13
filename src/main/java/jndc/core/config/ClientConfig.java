package jndc.core.config;

import jndc.utils.ApplicationExit;
import jndc.utils.InetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;


/**
 * JNDC client config
 */
public class ClientConfig implements ParameterVerification {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String serverIp;

    private int serverPort;

    private List<ClientServiceDescription> clientServiceDescriptions;//service list

    private Map<String, ClientServiceDescription> clientServiceDescriptionMap;//service map


    /* -----------------prepare file----------------- */

    private InetAddress serverIpAddress;

    private InetSocketAddress serverIpSocketAddress;


    @Override
    public void performParameterVerification() {
        serverIpAddress = InetUtils.getByStringIpAddress(serverIp);
        serverIpSocketAddress = new InetSocketAddress(serverIpAddress, serverPort);


        if (clientServiceDescriptions != null) {

            clientServiceDescriptionMap = new HashMap<>();
            clientServiceDescriptions.forEach(x -> {
                if (x.isServiceEnable()) {


                    if (clientServiceDescriptionMap.containsKey(x.getUniqueTag())) {
                        logger.error("duplicate service:" + x.getUniqueTag());
                        ApplicationExit.exit();
                    }
                    x.performParameterVerification();
                    clientServiceDescriptionMap.put(x.getUniqueTag(), x);

                }
            });
        }

    }


    public Logger getLogger() {
        return logger;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ClientServiceDescription> getClientServiceDescriptions() {
        return clientServiceDescriptions;
    }

    public void setClientServiceDescriptions(List<ClientServiceDescription> clientServiceDescriptions) {
        this.clientServiceDescriptions = clientServiceDescriptions;
    }

    public Map<String, ClientServiceDescription> getClientServiceDescriptionMap() {
        return clientServiceDescriptionMap;
    }

    public void setClientServiceDescriptionMap(Map<String, ClientServiceDescription> clientServiceDescriptionMap) {
        this.clientServiceDescriptionMap = clientServiceDescriptionMap;
    }

    public InetAddress getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(InetAddress serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public InetSocketAddress getServerIpSocketAddress() {
        return serverIpSocketAddress;
    }

    public void setServerIpSocketAddress(InetSocketAddress serverIpSocketAddress) {
        this.serverIpSocketAddress = serverIpSocketAddress;
    }
}
