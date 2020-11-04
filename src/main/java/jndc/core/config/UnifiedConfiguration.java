package jndc.core.config;

import jndc.core.IpListChecker;
import jndc.core.UniqueBeanManage;
import jndc.utils.AESUtils;
import jndc.utils.ApplicationExit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UnifiedConfiguration implements ParameterVerification {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String secrete;

    private ServerConfig serverConfig;

    private ClientConfig clientConfig;

    private String[] blackList;

    private String[] whiteList;




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


        IpListChecker ipListChecker = UniqueBeanManage.getBean(IpListChecker.class);
        if (blackList==null){
            blackList=new String[0];
        }

        if (whiteList==null){
            whiteList=new String[0];
        }

        ipListChecker.loadRule(blackList,whiteList);
    }

    @Override
    public String toString() {
        return "UnifiedConfiguration{" +
                "serverConfig=" + serverConfig +
                ", clientConfig=" + clientConfig +
                '}';
    }


    public Logger getLogger() {
        return logger;
    }

    public String[] getBlackList() {
        return blackList;
    }

    public void setBlackList(String[] blackList) {
        this.blackList = blackList;
    }

    public String[] getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String[] whiteList) {
        this.whiteList = whiteList;
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
