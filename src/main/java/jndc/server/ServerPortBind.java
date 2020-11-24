package jndc.server;

import jndc.core.data_store.DSKey;
import jndc.core.data_store.DSTable;


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

    private int virtualPort; //0 normal port / 1 virtual port

    private String domainBind; //


    public String getDomainBind() {
        return domainBind;
    }

    public void setDomainBind(String domainBind) {
        this.domainBind = domainBind;
    }

    public int getVirtualPort() {
        return virtualPort;
    }

    public void setVirtualPort(int virtualPort) {
        this.virtualPort = virtualPort;
    }

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
