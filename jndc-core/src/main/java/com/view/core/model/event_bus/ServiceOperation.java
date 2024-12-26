package com.view.core.model.event_bus;

import com.view.core.enum_value.ServiceOperationTypes;
import com.view.core.model.local_service.LocalService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ServiceOperation {

    private String ndcServerId;

    private String operationType;

    private LocalService localService;

    public static ServiceOperation ofDeploy(LocalService localService) {
        ServiceOperation serviceOperation = new ServiceOperation();
        serviceOperation.setOperationType(ServiceOperationTypes.DEPLOY.value);
        serviceOperation.setLocalService(localService);
        return serviceOperation;
    }

    public static ServiceOperation ofWithdraw(LocalService localService) {
        ServiceOperation serviceOperation = new ServiceOperation();
        serviceOperation.setOperationType(ServiceOperationTypes.WITHDRAW.value);
        serviceOperation.setLocalService(localService);
        return serviceOperation;
    }

    public static ServiceOperation ofData(LocalService localService) {
        ServiceOperation serviceOperation = new ServiceOperation();
        serviceOperation.setOperationType(ServiceOperationTypes.DEPLOY.value);
        serviceOperation.setLocalService(localService);
        return serviceOperation;
    }


    public boolean isDeploy() {
        return ServiceOperationTypes.DEPLOY.value.equals(operationType);
    }

    public boolean isWithdraw() {
        return ServiceOperationTypes.WITHDRAW.value.equals(operationType);
    }

}
