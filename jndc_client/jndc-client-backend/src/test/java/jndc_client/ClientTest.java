package jndc_client;


import jndc.utils.ApplicationExit;
import jndc.utils.YmlParser;
import jndc_client.core.ClientDirectManager;
import jndc_client.core.JNDCClient;
import jndc_client.core.JNDCClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;


@Slf4j
public class ClientTest {

    @Test
    public void run() {

        ClientDirectManager.idPath = "C:\\Users\\liuqiwei\\Desktop\\client_id";
        ClientDirectManager.ymlConfig = "D:\\JAVA_WORK_SPACE\\jndc\\jndc_client\\jndc-client-backend\\src\\main\\resources\\conf\\config.yml";


        String ymlConfig = ClientDirectManager.ymlConfig;
        File file = new File(ymlConfig);

        YmlParser ymlParser = new YmlParser();
        JNDCClientConfig jndcClientConfig;
        try {
            jndcClientConfig = ymlParser.parseFile(file, JNDCClientConfig.class);
            jndcClientConfig.performParameterVerification();
            jndcClientConfig.loadClientId();
            log.info("client time out--->" + jndcClientConfig.getAutoReleaseTimeOut());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("config file:" + file + " parse fail：" + e);
            ApplicationExit.exit();
        }


        JNDCClient clientTest = new JNDCClient();
        clientTest.start();


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
