package com.view.jndc.core.v2.componet.netty.handler;

public interface TCPOperationCallback {

    void active(String sourceId);

    void dataRead(String sourceId, byte[] t);

    void inActive(String sourceId);
}
