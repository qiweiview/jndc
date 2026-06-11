package jndc_server.core;

import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.utils.PathUtils;
import jndc.web_support.config.ServeManageConfig;
import jndc.web_support.http_module.ManagementServer;
import jndc_server.config.JNDCServerConfig;
import jndc_server.core.app.JndcCoreServer;
import jndc_server.databases_object.ServerPortBind;
import jndc_server.web_support.http_module.JNDCHttpServer;

import java.io.File;

/**
 * 主服务
 */
public class JNDCServerApp {

    public JNDCServerApp() {
    }


    /**
     * do some init operation only for server
     */
    public void initBelongOnlyInServer() {
        ScheduledTaskCenter scheduledTaskCenter = UniqueBeanManage.getBean(ScheduledTaskCenter.class);
        scheduledTaskCenter.start();
    }


    public void createServer() {
        //服务端初始化事件
        initBelongOnlyInServer();


        //重置所有端口使用信息
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        dbWrapper.customExecute("update server_port_bind set port_enable = 0", null);


        JNDCServerConfig jndcServerConfig = UniqueBeanManage.getBean(JNDCServerConfig.class);
        ServeManageConfig manageConfig = jndcServerConfig.getManageConfig();

        //处理静态项目地址（前端产物随项目构建，放在运行目录下）
        String runtimeDir = System.getProperty("user.dir") + File.separator + "compare_dist";
        manageConfig.setAdminProjectPath(runtimeDir);


        //admin管理页面
        ManagementServer managementServer = new ManagementServer();
        managementServer.start(manageConfig);//start

        //核心服务
        JndcCoreServer jndcCoreServer = new JndcCoreServer();
        jndcCoreServer.start();

        //http层服务
        JNDCHttpServer jndcHttpServer = new JNDCHttpServer();
        jndcHttpServer.start();


    }


}
