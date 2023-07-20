package cn.view.jndc.server_sv.core;

import cn.view.jndc.server_sv.core.app.JndcCoreServer;
import cn.view.jndc.server_sv.databases_object.ServerPortBind;
import cn.view.jndc.server_sv.web_support.http_module.JNDCHttpServer;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;

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





        //核心服务
        JndcCoreServer jndcCoreServer = new JndcCoreServer(null);
        jndcCoreServer.start();

        //http层服务
        JNDCHttpServer jndcHttpServer = new JNDCHttpServer();
        jndcHttpServer.start();


    }


}
