package jndc_client.core;

import ch.qos.logback.classic.Level;
import jndc.core.UniqueBeanManage;
import jndc.utils.AESUtils;
import jndc.utils.ApplicationExit;
import jndc.utils.InetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * JNDC client config
 */
public class JNDCClientConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String secrete;

    private String loglevel;

    private String serverIp;

    private int serverPort;

    private List<ClientServiceDescription> clientServiceDescriptions;//service list

    private Map<String, ClientServiceDescription> clientServiceDescriptionMap;//service map


    /* -----------------prepare file----------------- */

    private InetAddress serverIpAddress;

    private InetSocketAddress serverIpSocketAddress;


    public void init(){
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.toLevel(getLoglevel()));



        //set secrete
        AESUtils.setKey(secrete.getBytes());


        //register bean,this will be replaced later
        UniqueBeanManage.registerBean(new JNDCClientConfigCenter());
    }

    /**
     * 参数校验
     */
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

        init();

    }


    //getter setter


    public String getSecrete() {
        return secrete;
    }

    public void setSecrete(String secrete) {
        this.secrete = secrete;
    }

    public String getLoglevel() {
        return loglevel;
    }

    public void setLoglevel(String loglevel) {
        this.loglevel = loglevel;
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
