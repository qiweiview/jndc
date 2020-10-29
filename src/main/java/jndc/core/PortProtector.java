package jndc.core;

import jndc.server.NDCServerConfigCenter;

/**
 * focus on TSAP
 */
public interface PortProtector {
    public void start(NDCMessageProtocol registerMessage, NDCServerConfigCenter ndcServerConfigCenter);

    public void shutDown();

    public void receiveMessage(NDCMessageProtocol ndcMessageProtocol);
}
