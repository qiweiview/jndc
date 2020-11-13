package jndc.server;

import jndc.core.data_store.DSKey;
import jndc.core.data_store.DSTable;


@DSTable(name = "server_port_bind")
public class ServerPortBind {

    @DSKey
    private String id;

    private String name;

    private int port;

    private int portEnable;//1 enable 0 disable


    @Override
    public String toString() {
        return "ServerPortBind{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", port=" + port +
                ", portEnable=" + portEnable +
                '}';
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
