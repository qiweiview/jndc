package jndc.core;

import jndc.client.JNDCClient;
import jndc.core.config.ClientConfig;
import jndc.core.config.ServerConfig;
import jndc.core.config.UnifiedConfiguration;
import jndc.server.JNDCServer;
import jndc.utils.LogPrint;
import jndc.utils.YmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;

import java.net.InetAddress;
import java.net.InetSocketAddress;



public class AppStart {
    private  static final Logger logger = LoggerFactory.getLogger(AppStart.class);
    
    private static  final String CLIENT_APP_TYPE="CLIENT_APP_TYPE";

    private static  final String SERVER_APP_TYPE="SERVER_APP_TYPE";

    public static final YmlParser ymlParser = new YmlParser();

    public static void main(String[] args) {
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
            System.exit(1);
        }




        String configFile = args[0];

        File file = new File(configFile);
        if (!file.exists()) {
            logger.error("can not found:" + file );
            System.exit(1);
        }


        UnifiedConfiguration unifiedConfiguration = null;
        try {
            unifiedConfiguration = ymlParser.parseFile(file, UnifiedConfiguration.class);
            UniqueBeanManage.registerBean(unifiedConfiguration);
        } catch (Exception e) {
            logger.error("parse config file:" + file + "fail" + e);
            System.exit(1);
        }

        String type = args[1];

        if (SERVER_APP_TYPE.equals(type)){
            ServerConfig serverConfig = unifiedConfiguration.getServerConfig();
            JNDCServer serverTest =new JNDCServer(serverConfig.getAdminPort());
            serverTest.createServer();
            return;
        }

        if (CLIENT_APP_TYPE.equals(type)){
            ClientConfig clientConfig = unifiedConfiguration.getClientConfig();
            int adminPort = clientConfig.getRemoteAdminPort();

            InetAddress ip = clientConfig.getRemoteInetAddress();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, adminPort);
            JNDCClient clientTest = new JNDCClient(inetSocketAddress);
            clientTest.createClient();
            return;
        }
        logger.error("unSupport type");
        System.exit(1);

    }
}
