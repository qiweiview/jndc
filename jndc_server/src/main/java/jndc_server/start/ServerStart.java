package jndc_server.start;//package jndc.core;


import jndc.utils.ApplicationExit;
import jndc.utils.PathUtils;
import jndc.utils.YmlParser;
import jndc_server.config.JNDCServerConfig;
import jndc_server.core.JNDCServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;


@Slf4j
public class ServerStart {

    public static final YmlParser ymlParser = new YmlParser();

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Good night everyone ");
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
        log.info(tag);


        String runTimePath = PathUtils.getRunTimePath();
        log.info("读取运行目录： " + runTimePath);

        InputStream resourceAsStream = ServerStart.class.getClassLoader().getResourceAsStream("config.yml");
        if (resourceAsStream == null) {
            log.error("读取配置文件失败,请检查resources目录下是否存在config.yml文件");
            ApplicationExit.exit();
        }


        JNDCServerConfig jndcServerConfig;
        try {
            jndcServerConfig = ymlParser.parseFile(resourceAsStream, JNDCServerConfig.class);
            if (jndcServerConfig == null) {
//                String configContent = FileUtils.readFileToString(file, "utf-8");
                log.error("please check the content:\n=====content_start=====\n" + IOUtils.toByteArray(resourceAsStream) + "\n=====content_end=====\n on config.yml");
//                log.error("yml配置文件解析异常");
                ApplicationExit.exit();
            }
            //设置运行目录
            jndcServerConfig.setRuntimeDir(runTimePath);

            //参数校验
            jndcServerConfig.performParameterVerification();

            //懒加载组件
            jndcServerConfig.lazyInitAfterVerification();
        } catch (Exception e) {
            log.error("解析配置文件失败" + e);
            ApplicationExit.exit();
        }


        //启动相关服务
        JNDCServer serverTest = new JNDCServer();
        serverTest.createServer();
        return;


    }
}
