package jndc_server.databases_object;

import jndc.core.data_store_support.DSFiled;
import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import lombok.Data;

@Data
@DSTable(name = "client_controlled_service")
public class ClientControlledServiceRecord {

    @DSKey(name = "id")
    private String id;

    @DSFiled(name = "client_id")
    private String clientId;

    @DSFiled(name = "service_name")
    private String serviceName;

    @DSFiled(name = "service_ip")
    private String serviceIp;

    @DSFiled(name = "service_port")
    private int servicePort;

    @DSFiled(name = "description")
    private String description;
}
