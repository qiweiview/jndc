package jndc_server.web_support.model.dto;

import jndc.core.message.TcpServiceDescription;
import lombok.Data;

import java.util.List;

@Data
public class ControlledServiceReplaceDTO {

    private String clientId;

    private List<TcpServiceDescription> services;
}
