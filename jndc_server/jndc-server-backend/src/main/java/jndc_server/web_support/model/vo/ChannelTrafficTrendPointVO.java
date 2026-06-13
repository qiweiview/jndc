package jndc_server.web_support.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelTrafficTrendPointVO {
    private long timestamp;
    private long clientToServerBytes;
    private long serverToClientBytes;
    private long totalBytes;
}
