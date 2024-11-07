package com.view.core.component.data_hub;

import com.view.core.protocol.NDCPacket;

public interface PackageHubI {

    public void postOnServer(NDCPacket ndcPacket);

    public void postOnClient(NDCPacket ndcPacket);
}
