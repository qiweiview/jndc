package com.view.core.client;

import com.view.core.utils.UniqueId;
import lombok.Data;

@Data
public abstract class ControllableClient {

    private String clientServiceId;

    private String clientServiceSessionId = UniqueId.generate();

    private String appServerSessionId;

    private String appServerId;

    public abstract void receiveData(byte[] data);

    public abstract void stop();
}
