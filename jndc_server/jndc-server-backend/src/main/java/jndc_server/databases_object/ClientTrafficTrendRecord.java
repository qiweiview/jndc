package jndc_server.databases_object;

import jndc.core.data_store_support.DSFiled;
import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import lombok.Data;

@Data
@DSTable(name = "client_traffic_trend_record")
public class ClientTrafficTrendRecord {

    @DSKey
    private String id;

    @DSFiled(name = "client_id")
    private String clientId;

    @DSFiled(name = "bucket_type")
    private String bucketType;

    @DSFiled(name = "bucket_start_at")
    private Long bucketStartAt;

    @DSFiled(name = "client_to_server_bytes")
    private Long clientToServerBytes;

    @DSFiled(name = "server_to_client_bytes")
    private Long serverToClientBytes;

    @DSFiled(name = "updated_at")
    private Long updatedAt;
}
