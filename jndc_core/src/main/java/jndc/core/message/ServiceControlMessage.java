package jndc.core.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ServiceControlMessage implements Serializable {

    private static final long serialVersionUID = 7450543471509130580L;

    private String clientId;

    private List<TcpServiceDescription> tcpServiceDescriptions;
}
