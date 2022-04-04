package jndc_server.exmaple;

import jndc.utils.ApplicationExit;
import jndc.utils.YmlParser;
import jndc_server.config.JNDCServerConfig;
import jndc_server.config.ServerRuntimeConfig;
import jndc_server.web_support.http_module.JNDCHttpServer;

import java.io.File;

public class Single_HttpServerTest {

    public static void main(String[] args) {

        ServerRuntimeConfig.DEBUG_MODEL=true;

        File file;

//         file = new File("D:\\NewWorkSpace\\Tools\\jndc\\jndc_server\\src\\main\\java\\jndc_server\\exmaple\\config_file\\config.yml");

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
            ApplicationExit.exit();
        }

        //deploy jndc-http server
        JNDCHttpServer jndcHttpServer=new JNDCHttpServer();
        jndcHttpServer.start();
    }
}
