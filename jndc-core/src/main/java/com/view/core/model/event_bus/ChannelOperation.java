package com.view.core.model.event_bus;

import com.view.core.enum_value.ChannelOperationTypes;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ChannelOperation {

    private String clientId;

    private String operationType;


    public static ChannelOperation ofInactive(String clientId) {
        ChannelOperation serviceOperation = new ChannelOperation();
        serviceOperation.setOperationType(ChannelOperationTypes.INACTIVE.value);
        serviceOperation.setClientId(clientId);
        return serviceOperation;
    }

    public boolean isInactive() {
        return ChannelOperationTypes.INACTIVE.value.equals(operationType);
    }
}
