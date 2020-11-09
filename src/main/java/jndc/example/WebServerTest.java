package jndc.example;

import jndc.core.UniqueBeanManage;
import jndc.core.config.UnifiedConfiguration;
import jndc.server.JNDCServer;
import jndc.utils.ApplicationExit;
import jndc.utils.YmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.core.MappingRegisterCenter;
import web.core.WebServer;

import java.io.File;

public class WebServerTest {

    private   static final Logger logger = LoggerFactory.getLogger(WebServerTest.class);

    public static void main(String[] args) {

//        File file = new File("D:\\NewWorkSpace\\Tools\\jndc\\src\\main\\java\\jndc\\example\\config_file\\config.yml");
//        YmlParser ymlParser = new YmlParser();
//        UnifiedConfiguration unifiedConfiguration = null;
//        try {
//            unifiedConfiguration = ymlParser.parseFile(file, UnifiedConfiguration.class);
//            unifiedConfiguration.performParameterVerification();
//            UniqueBeanManage.registerBean(unifiedConfiguration);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("config file:" + file + " parse fail：" + e);
//            ApplicationExit.exit();
//        }



        WebServer serverTest =new WebServer();

        serverTest.start();//start
    }


}
