package jndc.core.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 隧道开通消息
 */
@Data
public class OpenChannelMessage implements Serializable {
    private static final long serialVersionUID = 7315766480559203141L;

    private String auth;

    private String channelId;


}
