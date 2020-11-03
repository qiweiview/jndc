package jndc.example;


import jndc.client.JNDCClient;
import jndc.core.UniqueBeanManage;
import jndc.core.config.ClientConfig;
import jndc.core.config.UnifiedConfiguration;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;
import jndc.utils.YmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;


public class ClientTest {
    private   static final Logger logger = LoggerFactory.getLogger(ClientTest.class);

    public static void main(String[] args) {
        File file = new File("D:\\NewWorkSpace\\Tools\\jndc\\src\\main\\java\\jndc\\example\\config_file\\config.yml");
        YmlParser ymlParser = new YmlParser();
        UnifiedConfiguration unifiedConfiguration = null;
        try {
            unifiedConfiguration = ymlParser.parseFile(file, UnifiedConfiguration.class);
            UniqueBeanManage.registerBean(unifiedConfiguration);
        } catch (Exception e) {
            logger.debug("配置文件:" + file + "解析异常：" + e);
            System.exit(1);
        }

        ClientConfig clientConfig = unifiedConfiguration.getClientConfig();
        int adminPort = clientConfig.getRemoteAdminPort();

        InetAddress ip = clientConfig.getRemoteInetAddress();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, adminPort);

        JNDCClient clientTest = new JNDCClient(inetSocketAddress);
        clientTest.createClient();


    }


}
