package com.view.jndc.core.v2.componet.netty.handler;

public interface ServiceRequestHandleCallback {


    void active(String proxyId, String sourceId);

    void accept(String proxyId, String sourceId, byte[] data);

    void inActive(String proxyId, String sourceId);
}
