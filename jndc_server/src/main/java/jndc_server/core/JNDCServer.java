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
        //do server init
        initBelongOnlyInServer();


        //reset service bind state
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        dbWrapper.customExecute("update server_port_bind set portEnable = 0", null);

        //deploy the server management api
        WebServer serverTest = new WebServer();
        serverTest.start();//start

        //deploy jndc-core service
        JndcCoreServer jndcCoreServer=new JndcCoreServer();
        jndcCoreServer.start();

        //deploy jndc-http server
        JNDCHttpServer jndcHttpServer=new JNDCHttpServer();
        jndcHttpServer.start();


    }


}
