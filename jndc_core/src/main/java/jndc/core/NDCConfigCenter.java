package jndc.core;


public interface NDCConfigCenter {

    public void addMessageToSendQueue(NDCMessageProtocol ndcMessageProtocol);

    public void addMessageToReceiveQueue(NDCMessageProtocol ndcMessageProtocol);


}
