package jndc.core;

import jndc.client.JNDCClient;
import jndc.core.config.ClientConfig;
import jndc.core.config.ClientPortMapping;
import jndc.core.config.ServerConfig;
import jndc.core.config.UnifiedConfiguration;
import jndc.server.JNDCServer;
import jndc.utils.LogPrint;
import jndc.utils.YmlParser;

import java.beans.beancontext.BeanContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

public class AppStart {
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
        LogPrint.log(tag);


        if (args.length < 2) {
            LogPrint.err("启动参数缺失");
            System.exit(1);
        }




        String configFile = args[0];

        //configFile = "D:\\NewWorkSpace\\Tools\\jndc\\src\\main\\java\\jndc\\example\\config_file\\config.yml";
        File file = new File(configFile);
        if (!file.exists()) {
            System.out.println("配置文件:" + file + "不存在");
            System.exit(1);
        }


        UnifiedConfiguration unifiedConfiguration = null;
        try {
            unifiedConfiguration = ymlParser.parseFile(file, UnifiedConfiguration.class);
            UniqueBeanManage.registerBean(unifiedConfiguration);
        } catch (Exception e) {
            LogPrint.err("配置文件:" + file + "解析异常：" + e);
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
            String bindIp = clientConfig.getRemoteIp();

            InetAddress ip = null;
            try {
                ip= InetAddress.getByName(bindIp);
            } catch (UnknownHostException e) {
                LogPrint.err("unknown  remote host");
                System.exit(1);
            }
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, adminPort);
            JNDCClient clientTest = new JNDCClient(inetSocketAddress);
            clientTest.createClient();
            return;
        }
        LogPrint.err("unSupport type");
        System.exit(1);

    }
}
