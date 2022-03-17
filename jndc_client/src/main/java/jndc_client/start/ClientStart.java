package jndc_client.start;//package jndc.core;


import jndc.utils.ApplicationExit;
import jndc.utils.YmlParser;
import jndc_client.core.JNDCClient;
import jndc_client.core.JNDCClientConfig;
import jndc_client.http_support.ClientHttpManagement;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class ClientStart {

    public static  String CLIENT_ID;

    private  static final Logger logger = LoggerFactory.getLogger(ClientStart.class);


    public static final YmlParser ymlParser = new YmlParser();

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            logger.info("Good night everyone ");
        }));



        String tag = "\n" +
                "       _   _   _ _____   _____             _____ _      _____ ______ _   _ _______ \n" +
                "      | | | \\ | |  __ \\ / ____|           / ____| |    |_   _|  ____| \\ | |__   __|\n" +
                "      | | |  \\| | |  | | |       ______  | |    | |      | | | |__  |  \\| |  | |   \n" +
                "  _   | | | . ` | |  | | |      |______| | |    | |      | | |  __| | . ` |  | |   \n" +
                " | |__| | | |\\  | |__| | |____           | |____| |____ _| |_| |____| |\\  |  | |   \n" +
                "  \\____/  |_| \\_|_____/ \\_____|           \\_____|______|_____|______|_| \\_|  |_|   \n" +
                "                                                                                   \n" +
                "                                                                                   \n" +
                "client_id: ";



        if (args.length < 1) {
            logger.error("missing startup parameters");
            ApplicationExit.exit();
        }

        String configFile = args[0];


        File file = new File(configFile);
        if (!file.exists()) {
            logger.error("can not found:" + file );
            ApplicationExit.exit();
        }
        JNDCClientConfig jndcClientConfig = null;
        try {
            jndcClientConfig = ymlParser.parseFile(file, JNDCClientConfig.class);
            if (jndcClientConfig == null) {
                String configContent = FileUtils.readFileToString(file, "utf-8");
                logger.error("please check the content:\n=====content_start=====\n" + configContent + "\n=====content_end=====\n on config.yml" + file);
                ApplicationExit.exit();
            }
            jndcClientConfig.performParameterVerification();
            jndcClientConfig.setRuntimeDir(file.getParent());
            jndcClientConfig.loadClientId();
            logger.info(tag + CLIENT_ID);
            logger.info("client time out--->" + jndcClientConfig.getAutoReleaseTimeOut());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("parse config file:" + file + "fail" + e);
            ApplicationExit.exit();
        }

        //http管理端
        ClientHttpManagement clientHttpManagement = new ClientHttpManagement();
        clientHttpManagement.start();

        //核心服务
        JNDCClient jndcClient = new JNDCClient();
        jndcClient.start();


        return;


    }
}
