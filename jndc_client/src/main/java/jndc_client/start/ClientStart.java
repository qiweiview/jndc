package jndc_client.start;//package jndc.core;


import jndc.core.UniqueBeanManage;
import jndc.utils.ApplicationExit;
import jndc.utils.YmlParser;
import jndc_client.core.JNDCClient;
import jndc_client.core.JNDCClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class ClientStart {


    private  static final Logger logger = LoggerFactory.getLogger(ClientStart.class);


    public static final YmlParser ymlParser = new YmlParser();

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            logger.info("Good night everyone ");
        }));



        String tag = "\n" +
                "                                                               \n" +
                "         ,---._                   ,--.                         \n" +
                "       .-- -.' \\                ,--.'|    ,---,      ,----..   \n" +
                "       |    |   :           ,--,:  : |  .'  .' `\\   /   /   \\  \n" +
                "       :    ;   |        ,`--.'`|  ' :,---.'     \\ |   :     : \n" +
                "       :        |        |   :  :  | ||   |  .`\\  |.   |  ;. / \n" +
                "       |    :   :        :   |   \\ | ::   : |  '  |.   ; /--`  \n" +
                "       :                 |   : '  '; ||   ' '  ;  :;   | ;     \n" +
                "       |    ;   |        '   ' ;.    ;'   | ;  .  ||   : |     \n" +
                "   ___ l                 |   | | \\   ||   | :  |  '.   | '___  \n" +
                " /    /\\    J   :        '   : |  ; .''   : | /  ; '   ; : .'| \n" +
                "/  ../  `..-    ,        |   | '`--'  |   | '` ,/  '   | '/  : \n" +
                "\\    \\         ;         '   : |      ;   :  .'    |   :    /  \n" +
                " \\    \\      ,'          ;   |.'      |   ,.'       \\   \\ .'   \n" +
                "  \"---....--'            '---'        '---'          `---`     \n" +
                "                                                               \n";
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
        JNDCClientConfig jndcClientConfig = null;
        try {
            jndcClientConfig = ymlParser.parseFile(file, JNDCClientConfig.class);
            jndcClientConfig.performParameterVerification();
            UniqueBeanManage.registerBean(jndcClientConfig);
        } catch (Exception e) {
            logger.error("parse config file:" + file + "fail" + e);
            ApplicationExit.exit();
        }




        JNDCClient serverTest =new JNDCClient();
        serverTest.createClient();
        return;



    }
}
