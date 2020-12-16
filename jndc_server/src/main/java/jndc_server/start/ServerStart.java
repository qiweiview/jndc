package jndc_server.start;//package jndc.core;


import jndc.core.UniqueBeanManage;
import jndc_server.core.JNDCServer;
import jndc.utils.ApplicationExit;
import jndc.utils.YmlParser;
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




        JNDCServer serverTest =new JNDCServer();
        serverTest.createServer();
        return;



    }
}
