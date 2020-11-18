package jndc.core.config;

import jndc.core.IpChecker;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store.DBWrapper;
import jndc.server.IpFilterRule4V;
import jndc.utils.AESUtils;
import jndc.utils.ApplicationExit;
import jndc.utils.LogPrint;
import jndc.utils.UUIDSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

public class UnifiedConfiguration implements ParameterVerification {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String runtimeDir;

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


        //set runtime dir
        File file = new File("");
        String absolutePath = file.getAbsolutePath();
        LogPrint.info("runtimePath:"+absolutePath);
        setRuntimeDir(absolutePath);


        serverConfig.performParameterVerification();
        clientConfig.performParameterVerification();


    }

    @Override
    public void lazyInitAfterVerification() {
        serverConfig.lazyInitAfterVerification();
        clientConfig.lazyInitAfterVerification();
    }

    @Override
    public String toString() {
        return "UnifiedConfiguration{" +
                "serverConfig=" + serverConfig +
                ", clientConfig=" + clientConfig +
                '}';
    }

    public String getRuntimeDir() {
        return runtimeDir;
    }

    public void setRuntimeDir(String runtimeDir) {
        this.runtimeDir = runtimeDir;
    }

    public Logger getLogger() {
        return logger;
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
