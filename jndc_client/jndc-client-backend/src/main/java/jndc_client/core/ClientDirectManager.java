package jndc_client.core;

import jndc.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class ClientDirectManager {
    public static String basePath;

    public static String ymlConfig;

    public static String idPath;

    public static String authKeyPath;


    static {
        basePath = PathUtils.getClientWorkspace();

        ymlConfig = basePath + File.separator + "conf" + File.separator + "config.yml";

        idPath = basePath + File.separator + "conf" + File.separator + "client_id";

        authKeyPath = basePath + File.separator + "conf" + File.separator + "client_auth_key";


        log.info("=======================使用以下路径启动=======================");
        log.info("basePath: " + basePath);
        log.info("ymlConfig: " + ymlConfig);
        log.info("idPath: " + idPath);
        log.info("authKeyPath: " + authKeyPath);
    }
}
