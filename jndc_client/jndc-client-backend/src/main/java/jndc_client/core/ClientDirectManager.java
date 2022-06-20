package jndc_client.core;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class ClientDirectManager {
    public static String basePath;

    public static String ymlConfig;

    public static String idPath;


    static {
        basePath = System.getProperty("user.dir");

        ymlConfig = basePath + File.separator + ".." + File.separator + "conf" + File.separator + "config.yml";

        idPath = basePath + File.separator + ".." + File.separator + "conf" + File.separator + "client_id";


        log.info("=======================使用以下路径启动=======================");
        log.info("basePath: " + basePath);
        log.info("ymlConfig: " + ymlConfig);
        log.info("idPath: " + idPath);
    }
}
