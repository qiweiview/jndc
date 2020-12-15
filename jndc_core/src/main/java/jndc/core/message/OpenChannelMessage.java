package jndc.core.message;

import jndc.core.TcpServiceDescription;

import java.io.Serializable;
import java.util.List;

/**
 * 请求响应共体
 */
public class OpenChannelMessage implements Serializable {
    private static final long serialVersionUID = 7315766480559203141L;

    private String auth;

    private String channelId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
