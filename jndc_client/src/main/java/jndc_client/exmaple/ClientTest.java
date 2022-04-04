package jndc_client.exmaple;


import jndc.utils.ApplicationExit;
import jndc.utils.YmlParser;
import jndc_client.core.JNDCClient;
import jndc_client.core.JNDCClientConfig;
import jndc_client.http_support.ClientHttpManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class ClientTest {
    private   static final Logger logger = LoggerFactory.getLogger(ClientTest.class);

    public static void main(String[] args) {

        String devPath = System.getProperty("user.dir") + File.separator + "jndc_client\\src\\main\\resources\\config.yml";
        File file = new File(devPath);

        YmlParser ymlParser = new YmlParser();
        JNDCClientConfig jndcClientConfig = null;
        try {
            jndcClientConfig = ymlParser.parseFile(file, JNDCClientConfig.class);
            jndcClientConfig.performParameterVerification();
            jndcClientConfig.setRuntimeDir(file.getParent());
            jndcClientConfig.loadClientId();
            logger.info("client time out--->" + jndcClientConfig.getAutoReleaseTimeOut());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("config file:" + file + " parse fail：" + e);
            ApplicationExit.exit();
        }

        //http管理端
        ClientHttpManagement clientHttpManagement = new ClientHttpManagement();
        clientHttpManagement.start();

        JNDCClient clientTest = new JNDCClient();
        clientTest.start();


    }


}
