package jndc_client.core;

import jndc.core.UniqueBeanManage;
import jndc.utils.PathUtils;
import jndc.web_support.config.ServeManageConfig;
import jndc.web_support.http_module.ManagementServer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;


/**
 * 主服务
 */
@Slf4j
public class JNDClientApp {


    public void createClient() {

        JNDCClientConfig jndcClientConfig = UniqueBeanManage.getBean(JNDCClientConfig.class);


        ServeManageConfig manageConfig = jndcClientConfig.getManageConfig();

        if (manageConfig.isAdminEnable()) {
            //todo 处理静态项目地址
            String runTimePath = PathUtils.getRunTimePath();
            String p1 = runTimePath + File.separator + ".." + File.separator + "compare_dist";
            String p2 = System.getProperty("user.dir") + File.separator + "target" + File.separator + "jndc_client" + File.separator + "compare_dist";
            String runtimeDir = PathUtils.findExistPath(p1, p2);
            manageConfig.setAdminProjectPath(runtimeDir);
        }


        //http管理端
        ManagementServer managementServer = new ManagementServer();
        managementServer.start(manageConfig);//start

        //核心服务
        JNDCClient jndcClient = new JNDCClient();
        jndcClient.start();


    }


}
