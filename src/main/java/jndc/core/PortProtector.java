package jndc.core;

import jndc.server.NDCServerConfigCenter;

/**
 * focus on TSAP
 */
public interface PortProtector {
    public void start(NDCMessageProtocol registerMessage, NDCServerConfigCenter ndcServerConfigCenter);

    public void shutDownBeforeCreate();

    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol);
}
