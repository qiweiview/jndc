package jndc.core;

import jndc.client.JNDCClient;
import jndc.core.config.UnifiedConfiguration;
import jndc.server.JNDCServer;
import jndc.utils.ApplicationExit;
import jndc.utils.LogPrint;
import jndc.utils.YmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;


public class AppStart {
    public static String runType;

    private  static final Logger logger = LoggerFactory.getLogger(AppStart.class);
    
    public static  final String CLIENT_APP_TYPE="CLIENT_APP_TYPE";

    public static  final String SERVER_APP_TYPE="SERVER_APP_TYPE";



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


        if (args.length < 2) {
            logger.error("missing startup parameters");
            ApplicationExit.exit();
        }




        String configFile = args[0];
        runType=args[1];

        File file = new File(configFile);
        if (!file.exists()) {
            logger.error("can not found:" + file );
            ApplicationExit.exit();
        }
        UnifiedConfiguration unifiedConfiguration = null;
        try {
            unifiedConfiguration = ymlParser.parseFile(file, UnifiedConfiguration.class);
            unifiedConfiguration.setThisAppType(args[1]);
            unifiedConfiguration.performParameterVerification();
            UniqueBeanManage.registerBean(unifiedConfiguration);
            unifiedConfiguration.lazyInitAfterVerification();
        } catch (Exception e) {
            logger.error("parse config file:" + file + "fail" + e);
            ApplicationExit.exit();
        }




        if (SERVER_APP_TYPE.equals(unifiedConfiguration.getThisAppType())){
            JNDCServer serverTest =new JNDCServer();
            serverTest.createServer();
            return;
        }

        if (CLIENT_APP_TYPE.equals(unifiedConfiguration.getThisAppType())){
            JNDCClient clientTest = new JNDCClient();
            clientTest.createClient();
            return;
        }
        logger.error("unSupport type");
        ApplicationExit.exit();

    }
}
