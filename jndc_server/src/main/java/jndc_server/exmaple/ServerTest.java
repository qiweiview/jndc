package jndc_server.exmaple;

import jndc.core.UniqueBeanManage;

import jndc.utils.ApplicationExit;
import jndc.utils.YmlParser;
import jndc_server.config.ServerRuntimeConfig;
import jndc_server.core.JNDCServer;
import jndc_server.core.JNDCServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ServerTest {

    private   static final Logger logger = LoggerFactory.getLogger(ServerTest.class);

    public static void main(String[] args) {

        ServerRuntimeConfig.DEBUG_MODEL=true;

        File file = new File("D:\\NewWorkSpace\\Tools\\jndc\\jndc_server\\src\\main\\java\\jndc_server\\exmaple\\config_file\\config.yml");
        file=new File("D:\\JAVA_WORK_SPACE\\jndc\\jndc_server\\src\\main\\java\\jndc_server\\exmaple\\config_file\\config.yml");

        YmlParser ymlParser = new YmlParser();
        JNDCServerConfig jndcServerConfig = null;
        try {
            jndcServerConfig = ymlParser.parseFile(file, JNDCServerConfig.class);
            jndcServerConfig.setRuntimeDir(file.getParent());
            jndcServerConfig.performParameterVerification();
            jndcServerConfig.lazyInitAfterVerification();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("config file:" + file + " parse fail：" + e);
            ApplicationExit.exit();
        }
        JNDCServer serverTest =new JNDCServer();

        serverTest.createServer();//start
    }


}
