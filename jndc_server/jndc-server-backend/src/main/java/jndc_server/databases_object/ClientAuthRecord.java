package jndc_server.databases_object;

import jndc.core.data_store_support.DSFiled;
import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import lombok.Data;

@Data
@DSTable(name = "client_auth_record")
public class ClientAuthRecord {
    @DSKey(name = "client_id")
    private String clientId;

    @DSFiled(name = "client_auth_key")
    private String clientAuthKey;

    @DSFiled(name = "auth_mode")
    private Integer authMode;
}
