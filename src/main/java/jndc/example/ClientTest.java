package jndc.example;


import jndc.client.JNDCClient;
import jndc.core.UniqueBeanManage;
import jndc.core.config.UnifiedConfiguration;
import jndc.utils.ApplicationExit;
import jndc.utils.YmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;



public class ClientTest {
    private   static final Logger logger = LoggerFactory.getLogger(ClientTest.class);

    public static void main(String[] args) {
        File file = new File("D:\\NewWorkSpace\\Tools\\jndc\\src\\main\\java\\jndc\\example\\config_file\\config.yml");
        YmlParser ymlParser = new YmlParser();
        UnifiedConfiguration unifiedConfiguration = null;
        try {
            unifiedConfiguration = ymlParser.parseFile(file, UnifiedConfiguration.class);
            unifiedConfiguration.performParameterVerification();
            UniqueBeanManage.registerBean(unifiedConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("config file:" + file + " parse fail：" + e);
            ApplicationExit.exit();
        }


        JNDCClient clientTest = new JNDCClient();
        clientTest.createClient();


    }


}
