package jndc_server.web_support.model.dto;

public class ServiceBindDTO {
    private String id;

    private String remark;

    private String serverPortId;

    private String serviceId;

    private String routeTo;

    private String enableDateRange;

    @Override
    public String toString() {
        return "ServiceBindDTO{" +
                "id='" + id + '\'' +
                ", remark='" + remark + '\'' +
                ", serverPortId='" + serverPortId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", routeTo='" + routeTo + '\'' +
                ", enableDateRange='" + enableDateRange + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEnableDateRange() {
        return enableDateRange;
    }

    public void setEnableDateRange(String enableDateRange) {
        this.enableDateRange = enableDateRange;
    }

    public String getServerPortId() {
        return serverPortId != null ? serverPortId : id;
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

    public String getRouteTo() {
        return routeTo;
    }

    public void setRouteTo(String routeTo) {
        this.routeTo = routeTo;
    }
}
