package jndc.core.message;

import jndc.core.TcpServiceDescription;

import java.io.Serializable;
import java.util.List;

/**
 * 请求响应共体
 */
public class RegistrationMessage implements Serializable {

    private static final long serialVersionUID = 2323315614144754699L;

    public transient static final byte TYPE_REGISTER = 0x00;

    public transient static final byte TYPE_UNREGISTER = 0x01;


    private byte type;

    private String auth;

    private List<TcpServiceDescription> tcpServiceDescriptions;

    private String message;

    private String channelId;

    public RegistrationMessage(byte type) {
        this.type = type;
    }


    //-------------------------- getter setter --------------------------


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public List<TcpServiceDescription> getTcpServiceDescriptions() {
        return tcpServiceDescriptions;
    }

    public void setTcpServiceDescriptions(List<TcpServiceDescription> tcpServiceDescriptions) {
        this.tcpServiceDescriptions = tcpServiceDescriptions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "RegistrationMessage{" +
                "auth='" + auth + '\'' +
                ", tcpServiceDescriptions=" + tcpServiceDescriptions +
                ", message='" + message + '\'' +
                '}';
    }
}
