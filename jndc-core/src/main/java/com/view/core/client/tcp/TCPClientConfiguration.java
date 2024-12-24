package com.view.core.client.tcp;

import lombok.Data;

import java.util.function.Consumer;

@Data
public class TCPClientConfiguration {
    private Consumer<byte[]> readCallBack;
}
