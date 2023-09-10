package com.view.jndc.core.v2.model.json_object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ChannelRegister extends JSONSerializable {
    private String channelId;


    @JsonIgnore
    private Map<String, ServiceRegister> serviceMap = new ConcurrentHashMap<>();

    public ServiceRegister getService(String serviceId) {
        return serviceMap.get(serviceId);
    }

    public void bindServiceOnClient(String serviceId, ServiceRegister serviceRegister) {
        serviceMap.put(serviceId, serviceRegister);

        //执行记录
        clientBindRecord(serviceId, serviceRegister);
    }


    /**
     * 绑定服务
     *
     * @param serviceId
     * @param serviceRegister
     */
    public void bindServiceOnServer(String serviceId, ServiceRegister serviceRegister) {
        serviceMap.put(serviceId, serviceRegister);

        //执行记录
        serverBindRecord(serviceId, serviceRegister);
    }


    /**
     * 关闭通道
     */
    public void shutdown() {
        serviceMap.forEach((k, v) -> {
            v.shutdown();
        });
    }


    /**
     * 获取通道下所有服务
     *
     * @return
     */
    protected List<ServiceRegister> listService() {
        List<ServiceRegister> list = new ArrayList<>();
        serviceMap.forEach((k, v) -> {
            list.add(v);
        });
        return list;
    }

    protected void clientBindRecord(String serviceId, ServiceRegister serviceRegister) {

    }


    protected void serverBindRecord(String serviceId, ServiceRegister serviceRegister) {

    }


}
