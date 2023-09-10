package com.view.jndc.core.v2.model.json_object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.view.jndc.core.v2.componet.netty.handler.ServiceRequestHandleCallback;
import com.view.jndc.core.v2.componet.server.ServiceProxy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class ServiceRegister extends JSONSerializable {
    /**
     * 通道源编号
     */
    private String sourceChannelId;

    /**
     * 服务编号
     */
    private String serviceId;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 目标域名
     */
    private String descHost;

    /**
     * 服务编号
     */
    private int port;


    @JsonIgnore
    private Map<String, ServiceProxy> proxyMap = new ConcurrentHashMap<>();

    @JsonIgnore
    private ServiceRequestHandleCallback callback;


    public void handleActive(String proxyId, String sourceId) {
        log.info("收到来自【代理器：" + proxyId + "】的【用户" + sourceId + "】连接建立数据");
        callback.active(proxyId, sourceId);
    }

    /**
     * 处理请求
     *
     * @param proxyId  代理编号
     * @param sourceId 调用者编号
     * @param data     数据
     */
    public void handleRequest(String proxyId, String sourceId, byte[] data) {
        log.info("收到来自【代理器：" + proxyId + "】的【用户" + sourceId + "】数据");
        callback.accept(proxyId, sourceId, data);
    }

    public void handleInActive(String proxyId, String sourceId) {
        log.info("收到来自【代理器：" + proxyId + "】的【用户" + sourceId + "】连接断开数据");
        callback.inActive(proxyId, sourceId);
    }


    public static ServiceRegister of(String serviceName, String descHost, int descPort) {
        String serviceId = ServiceRegister.generateId(serviceName, descHost, descPort);
        ServiceRegister serviceRegister = new ServiceRegister();
        serviceRegister.setServiceName(serviceName);
        serviceRegister.setDescHost(descHost);
        serviceRegister.setPort(descPort);
        serviceRegister.setServiceId(serviceId);
        return serviceRegister;
    }


    public static String generateId(String serviceName, String descHost, int descPort) {
        return serviceName + "://" + descHost + ":" + descPort;
    }


    public void registerProxy(String proxyId, ServiceProxy serviceProxy) {
        proxyMap.put(proxyId, serviceProxy);

        //执行记录
        registerProxyRecord(proxyId, serviceProxy);
    }

    protected void registerProxyRecord(String serviceId, ServiceProxy serviceProxy) {

    }


    public void shutdown() {
        proxyMap.forEach((k, b) -> {
            b.unbind();
        });
    }

    public void setDataHandler(ServiceRequestHandleCallback callback) {
        this.callback = callback;
    }


}
