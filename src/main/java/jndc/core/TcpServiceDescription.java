package jndc.core;

import java.io.Serializable;

public class TcpServiceDescription implements Serializable {


    private static final long serialVersionUID = -6570101717300836163L;

    private int port;

    private String ip;

    private String name;

    private String description;

    private String belongContextIp;

    public String getBelongContextIp() {
        return belongContextIp;
    }

    public void setBelongContextIp(String belongContextIp) {
        this.belongContextIp = belongContextIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "TcpServiceDescription{" +
                "port=" + port +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
