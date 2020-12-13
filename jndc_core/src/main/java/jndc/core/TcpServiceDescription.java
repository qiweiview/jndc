package jndc.core;



import java.io.Serializable;


/**
 * the description of service supported by client
 */
public class TcpServiceDescription implements Serializable {


    private static final long serialVersionUID = -6570101717300836163L;

    private String id;

    private int port;

    private String ip;//the service ip in the client net before NAT

    private String name;

    private String description;



    //getter setter

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
