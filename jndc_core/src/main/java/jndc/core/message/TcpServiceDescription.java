package jndc.core.message;



import lombok.Data;

import java.io.Serializable;


/**
 * 服务描述
 */
@Data
public class TcpServiceDescription implements Serializable {


    private static final long serialVersionUID = -6570101717300836163L;

    //服务编号
    private String id;

    //本地客户端服务端口
    private int servicePort;

    //本地客户端服务ip
    private String serviceIp;

    //服务名称
    private String serviceName;

    //服务描述
    private String description;


}
