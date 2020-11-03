package jndc.core.config;

import jndc.utils.AESUtils;
import jndc.utils.ApplicationExit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnifiedConfiguration implements ParameterVerification {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String secrete;

    private ServerConfig serverConfig;

    private ClientConfig clientConfig;


    @Override
    public void performParameterVerification() {
        if (null == secrete) {
            logger.error("the secrete not be found in config file");
            ApplicationExit.exit();
        } else {
            AESUtils.setKey(secrete.getBytes());
        }

        serverConfig.performParameterVerification();
        clientConfig.performParameterVerification();
    }

    @Override
    public String toString() {
        return "UnifiedConfiguration{" +
                "serverConfig=" + serverConfig +
                ", clientConfig=" + clientConfig +
                '}';
    }

    public String getSecrete() {
        return secrete;
    }

    public void setSecrete(String secrete) {
        this.secrete = secrete;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
