package jndc.core.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 服务注册消息
 */
@Data
public class RegistrationMessage implements Serializable {

    private static final long serialVersionUID = 2323315614144754699L;

    public transient static final byte TYPE_REGISTER = 0x00;

    public transient static final byte TYPE_UNREGISTER = 0x01;


    private byte type;

    private String auth;

    private List<TcpServiceDescription> tcpServiceDescriptions;

    private String message;

    //客户端唯一编号
    private String channelId;

    public RegistrationMessage(byte type) {
        this.type = type;
    }



}
