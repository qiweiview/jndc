package jndc_server.start;//package jndc.core;


import jndc.utils.ApplicationExit;
import jndc.utils.YmlParser;
import jndc_server.core.JNDCServer;
import jndc_server.core.JNDCServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class ServerStart {


    private  static final Logger logger = LoggerFactory.getLogger(ServerStart.class);





    public static final YmlParser ymlParser = new YmlParser();

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            logger.info("Good night everyone ");
        }));



        String tag = "\n" +
                "       _   _   _ _____   _____             _____ ______ _______      ________ _____  \n" +
                "      | | | \\ | |  __ \\ / ____|           / ____|  ____|  __ \\ \\    / /  ____|  __ \\ \n" +
                "      | | |  \\| | |  | | |       ______  | (___ | |__  | |__) \\ \\  / /| |__  | |__) |\n" +
                "  _   | | | . ` | |  | | |      |______|  \\___ \\|  __| |  _  / \\ \\/ / |  __| |  _  / \n" +
                " | |__| | | |\\  | |__| | |____            ____) | |____| | \\ \\  \\  /  | |____| | \\ \\ \n" +
                "  \\____/  |_| \\_|_____/ \\_____|          |_____/|______|_|  \\_\\  \\/   |______|_|  \\_\\\n" +
                "                                                                                     \n" +
                "                                                                                     ";
        logger.info(tag);


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



        JNDCServerConfig jndcServerConfig = null;
        try {
            jndcServerConfig = ymlParser.parseFile(file, JNDCServerConfig.class);
            jndcServerConfig.setRuntimeDir(file.getParent());
            jndcServerConfig.performParameterVerification();
            jndcServerConfig.lazyInitAfterVerification();
        } catch (Exception e) {
            logger.error("parse config file:" + file + "fail" + e);
            ApplicationExit.exit();
        }


        //启动相关服务
        JNDCServer serverTest =new JNDCServer();
        serverTest.createServer();
        return;



    }
}
