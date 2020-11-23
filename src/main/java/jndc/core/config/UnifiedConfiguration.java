package jndc.core.config;

import ch.qos.logback.classic.Level;
import jndc.core.AppStart;
import jndc.utils.AESUtils;
import jndc.utils.ApplicationExit;
import jndc.utils.LogPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class UnifiedConfiguration implements ParameterVerification {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String UN_SUPPORT_VALUE="jndc";

    private  String thisAppType;

    private String runtimeDir;

    private String secrete;

    private ServerConfig serverConfig;

    private ClientConfig clientConfig;

    private String loglevel;






    @Override
    public void performParameterVerification() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.toLevel(getLoglevel()));


        if (null == secrete) {
            logger.error("the secrete not be found in config file");
            ApplicationExit.exit();
        } else {
            if (UN_SUPPORT_VALUE.equals(secrete)){
                LogPrint.err("the default secrete 'jndc' is not safe,please edit the config file and retry");
                ApplicationExit.exit();
            }

            AESUtils.setKey(secrete.getBytes());
        }


        //set runtime dir
        File file = new File("");
        String absolutePath = file.getAbsolutePath();
        LogPrint.info("the app runtimePath is: "+absolutePath);
        setRuntimeDir(absolutePath);

        if (AppStart.SERVER_APP_TYPE.endsWith(getThisAppType())){
            serverConfig.performParameterVerification();
        }

        if (AppStart.CLIENT_APP_TYPE.endsWith(getThisAppType())){
            clientConfig.performParameterVerification();
        }




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



    //getter setter


    public String getLoglevel() {
        return loglevel;
    }

    public void setLoglevel(String loglevel) {
        this.loglevel = loglevel;
    }

    public static String getUnSupportValue() {
        return UN_SUPPORT_VALUE;
    }

    public String getThisAppType() {
        return thisAppType;
    }

    public void setThisAppType(String thisAppType) {
        this.thisAppType = thisAppType;
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
