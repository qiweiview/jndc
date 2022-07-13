package jndc_client.start;//package jndc.core;


import jndc.utils.ApplicationExit;
import jndc.utils.PathUtils;
import jndc.utils.YmlParser;
import jndc_client.core.JNDCClientConfig;
import jndc_client.core.JNDClientApp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;


@Slf4j
public class ClientStart {
    //全局id
    public static String CLIENT_ID;

    public static final YmlParser ymlParser = new YmlParser();

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Good night everyone ");
        }));


        String runTimePath = PathUtils.getRunTimePath();
        log.info("读取运行目录： " + runTimePath);

        String configPath = runTimePath + File.separator + ".." + File.separator + "conf" + File.separator + "config.yml";
        File file = new File(configPath);
        if (!file.exists()) {
            log.error("读取配置文件失败,请检查 " + configPath + " 目录下是否存在");
            ApplicationExit.exit();
        }


        JNDCClientConfig jndcClientConfig = null;
        try {
            jndcClientConfig = ymlParser.parseFile(file, JNDCClientConfig.class);
            if (jndcClientConfig == null) {
                log.error("please check the content:\n=====content_start=====\n" + new String(IOUtils.toByteArray(new FileInputStream(file))) + "\n=====content_end=====\n on config.yml");
                ApplicationExit.exit();
            }
            jndcClientConfig.performParameterVerification();
            jndcClientConfig.loadClientId();
            log.info("client time out--->" + jndcClientConfig.getAutoReleaseTimeOut());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("parse config file fail" + e);
            ApplicationExit.exit();
        }


        JNDClientApp jndClientApp = new JNDClientApp();
        jndClientApp.createClient();

//        //http管理端
//        ManagementServer managementServer = new ManagementServer();
//        managementServer.start();//start
//
//        //核心服务
//        JNDCClient jndcClient = new JNDCClient();
//        jndcClient.start();

    }
}
