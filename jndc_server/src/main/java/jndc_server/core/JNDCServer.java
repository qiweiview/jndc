package jndc_server.core;

import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc_server.core.app.JndcCoreServer;
import jndc_server.databases_object.ServerPortBind;
import jndc_server.web_support.core.WebServer;
import jndc_server.web_support.http_module.JNDCHttpServer;

/**
 * 主服务
 */
public class JNDCServer {

    public JNDCServer() {
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
        dbWrapper.customExecute("update server_port_bind set portEnable = 0", null);

        //admin管理页面
        WebServer webServer = new WebServer();
        webServer.start();//start

        //核心服务
        JndcCoreServer jndcCoreServer = new JndcCoreServer();
        jndcCoreServer.start();

        //http层服务
        JNDCHttpServer jndcHttpServer = new JNDCHttpServer();
        jndcHttpServer.start();


    }


}
