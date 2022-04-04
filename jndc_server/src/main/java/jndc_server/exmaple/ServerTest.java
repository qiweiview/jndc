package jndc_server.exmaple;

import jndc.utils.ApplicationExit;
import jndc.utils.PathUtils;
import jndc.utils.YmlParser;
import jndc_server.config.JNDCServerConfig;
import jndc_server.config.ServerRuntimeConfig;
import jndc_server.core.JNDCServer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class ServerTest {


    public static void main(String[] args) {

        ServerRuntimeConfig.DEBUG_MODEL = true;

        String runTimePath = PathUtils.getRunTimePath();
        log.info("读取运行目录： " + runTimePath);

        String devPath = System.getProperty("user.dir") + File.separator + "jndc_server\\src\\main\\resources\\config.yml";
        File file = new File(devPath);


        YmlParser ymlParser = new YmlParser();
        JNDCServerConfig jndcServerConfig = null;
        try {
            jndcServerConfig = ymlParser.parseFile(file, JNDCServerConfig.class);
            jndcServerConfig.setRuntimeDir(runTimePath);
            jndcServerConfig.performParameterVerification();
            jndcServerConfig.lazyInitAfterVerification();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("config file parse fail：" + e);
            ApplicationExit.exit();
        }
        JNDCServer serverTest =new JNDCServer();

        serverTest.createServer();//start
    }


}
