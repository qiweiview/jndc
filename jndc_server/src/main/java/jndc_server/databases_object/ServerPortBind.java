package jndc_server.databases_object;

import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import lombok.Data;


/**
 * 服务端端口监听描述信息表
 */
@Data
@DSTable(name = "server_port_bind")
public class ServerPortBind {

    @DSKey
    private String id;

    private String name;

    private int port;

    private String enableDateRange;

    //客户端唯一编号
    private String bindClientId;

    private int portEnable;//1 enable 0 disable 2 preparing

    //服务路由路径
    private String routeTo;

    /**
     * change state to enable
     */
    public void bindEnable(){
        setPortEnable(1);
    }

    /**
     * change state to disable
     */
    public void bindDisable(){
        setPortEnable(0);
    }

    /**
     * change state to preparing
     */
    public void bindPreparing(){
        setPortEnable(2);
    }


}
