package jndc.example;

import jndc.core.UniqueBeanManage;
import jndc.core.config.ServerConfig;
import jndc.core.config.UnifiedConfiguration;
import jndc.server.JNDCServer;
import jndc.utils.LogPrint;
import jndc.utils.YmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ServerTest {

    private   static final Logger logger = LoggerFactory.getLogger(ServerTest.class);

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
        ServerConfig serverConfig = unifiedConfiguration.getServerConfig();

        JNDCServer serverTest =new JNDCServer(serverConfig.getAdminPort());
        serverTest.createServer();
    }


}
