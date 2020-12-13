package jndc_server.core;

import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;


/**
 * the description of the server port listening
 */
@DSTable(name = "server_port_bind")
public class ServerPortBind {

    @DSKey
    private String id;

    private String name;

    private int port;

    private int portEnable;//1 enable 0 disable 2 preparing

    private String routeTo;


    public String getRouteTo() {
        return routeTo;
    }

    public void setRouteTo(String routeTo) {
        this.routeTo = routeTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPortEnable() {
        return portEnable;
    }

    public void setPortEnable(int portEnable) {
        this.portEnable = portEnable;
    }
}
