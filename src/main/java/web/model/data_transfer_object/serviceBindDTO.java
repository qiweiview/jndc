package web.model.data_transfer_object;

public class serviceBindDTO {
    private String serverPortId;

    private String serviceId;

    @Override
    public String toString() {
        return "serviceBindDTO{" +
                "serverPortId='" + serverPortId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }

    public String getServerPortId() {
        return serverPortId;
    }

    public void setServerPortId(String serverPortId) {
        this.serverPortId = serverPortId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
