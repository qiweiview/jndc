package jndc.core.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 隧道开通消息
 */
@Data
public class OpenChannelMessage implements Serializable {
    private static final long serialVersionUID = 7315766480559203141L;

    public static final int SELF_MANAGED = 0;

    public static final int FULL_AUTHORIZED = 1;

    private String auth;

    private String channelId;

    private String clientAuthKey;

    private int authMode = SELF_MANAGED;

}
