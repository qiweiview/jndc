package jndc_server.web_support.model.vo;

import lombok.Data;

@Data
public class ServerRuntimeInfoVO {
    private String bindIp;
    private int servicePort;
    private int managementApiPort;
    private int httpPort;
    private String secrete;
}
