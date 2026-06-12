package jndc_server.web_support.model.vo;

import jndc.core.message.TcpServiceDescription;
import lombok.Data;

import java.util.List;

@Data
public class ControlledServiceStateVO {

    private String clientId;

    private boolean online;

    private int authMode;

    private List<TcpServiceDescription> targetServices;

    private List<TcpServiceDescription> actualServices;
}
