package jndc_server.exmaple;

import jndc.utils.ApplicationExit;
import jndc.utils.PathUtils;
import jndc.utils.YmlParser;
import jndc_server.config.JNDCServerConfig;
import jndc_server.core.JNDCServerApp;
import jndc_server.web_support.http_module.ServerRuntimeConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;

@Slf4j
public class ServerTest {


    @Test
    public void start() {

        ServerRuntimeConfig.DEBUG_MODEL = true;

        String runTimePath = PathUtils.getRunTimePath();
        log.info("读取运行目录： " + runTimePath);

        String devPath = System.getProperty("user.dir") + File.separator + "src\\main\\resources\\conf\\config.yml";
        File file = new File(devPath);


        YmlParser ymlParser = new YmlParser();
        JNDCServerConfig jndcServerConfig;
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
        JNDCServerApp serverTest = new JNDCServerApp();

        serverTest.createServer();//start


        Thread thread = Thread.currentThread();
        synchronized (thread) {
            try {
                thread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


}
