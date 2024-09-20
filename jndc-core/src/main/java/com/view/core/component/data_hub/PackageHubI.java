package com.view.core.component.data_hub;

import com.view.core.protocol.NDCPacket;

public interface PackageHubI {

    public void publishOnServer(NDCPacket ndcPacket);

    public void publishOnClient(NDCPacket ndcPacket);
}
