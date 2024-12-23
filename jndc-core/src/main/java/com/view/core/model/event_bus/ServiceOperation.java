package com.view.core.model.event_bus;

import com.view.core.enum_value.ServiceOperationTypes;
import com.view.core.model.VirtualTCPService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ServiceOperation {

    private String ndcServerId;

    private String operationType;

    private VirtualTCPService virtualTCPService;

    public static ServiceOperation ofDeploy(VirtualTCPService virtualTCPService) {
        ServiceOperation serviceOperation = new ServiceOperation();
        serviceOperation.setOperationType(ServiceOperationTypes.DEPLOY.value);
        serviceOperation.setVirtualTCPService(virtualTCPService);
        return serviceOperation;
    }

    public static ServiceOperation ofWithdraw(VirtualTCPService virtualTCPService) {
        ServiceOperation serviceOperation = new ServiceOperation();
        serviceOperation.setOperationType(ServiceOperationTypes.WITHDRAW.value);
        serviceOperation.setVirtualTCPService(virtualTCPService);
        return serviceOperation;
    }

    public static ServiceOperation ofData(VirtualTCPService virtualTCPService) {
        ServiceOperation serviceOperation = new ServiceOperation();
        serviceOperation.setOperationType(ServiceOperationTypes.DEPLOY.value);
        serviceOperation.setVirtualTCPService(virtualTCPService);
        return serviceOperation;
    }


    public boolean isDeploy() {
        return ServiceOperationTypes.DEPLOY.value.equals(operationType);
    }

    public boolean isWithdraw() {
        return ServiceOperationTypes.WITHDRAW.value.equals(operationType);
    }

}
