package jndc_client;


import jndc.utils.ApplicationExit;
import jndc.utils.PathUtils;
import jndc.utils.YmlParser;
import jndc_client.core.ClientDirectManager;
import jndc_client.core.JNDCClientConfig;
import jndc_client.core.JNDClientApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;


@Slf4j
public class ClientTest {

    @Test
    public void run() {

        String devPath = PathUtils.getClientWorkspace() + File.separator + "conf" + File.separator + "config.yml";
        ClientDirectManager.idPath = PathUtils.getClientWorkspace() + File.separator + "conf" + File.separator + "client_id";
        ClientDirectManager.ymlConfig = devPath;


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


        JNDClientApp jndClientApp = new JNDClientApp();
        jndClientApp.createClient();


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
