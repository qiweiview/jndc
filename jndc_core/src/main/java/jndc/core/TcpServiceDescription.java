package jndc.core;



import lombok.Data;

import java.io.Serializable;


/**
 * the description of service supported by client
 */
@Data
public class TcpServiceDescription implements Serializable {


    private static final long serialVersionUID = -6570101717300836163L;

    //服务编号
    private String id;

    //本地客户端服务端口
    private int port;

    //本地客户端服务ip
    private String ip;

    //服务名称
    private String name;

    //服务描述
    private String description;


}
