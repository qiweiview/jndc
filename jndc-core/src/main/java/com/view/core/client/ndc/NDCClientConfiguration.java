package com.view.core.client.ndc;

import com.view.core.model.CheckAbleConfiguration;
import com.view.core.model.local_service.LocalService;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketHelper;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Data
@Slf4j
public class NDCClientConfiguration extends CheckAbleConfiguration {
    //IP
    private String serverHost;

    //端口
    private int serverPort;

    //唯一ID
    private String uniqueId;

    //重连次数(内存中)
    private volatile Integer reconnectTimes;

    //重连次数限制
    private Integer reconnectMaxTimes;

    //重试间隔
    private Integer reconnectInterval;

    //是否自动重连
    private Boolean autoReconnect;

    //重试中断(内存中)
    private Boolean retryBreak = false;

    private Thread waitingThread;

    /*------服务端本身------*/
    private Runnable startedCallback = EMPTY_CALLBACK;

    private Runnable stopCallback = EMPTY_CALLBACK;

    private Consumer<Exception> startFailCallback = EMPTY_CONSUMER(Exception.class);


    /*------服务端通讯------*/
    private Consumer<ChannelHandlerContext> connectActiveCallback = EMPTY_CONSUMER(ChannelHandlerContext.class);

    private BiConsumer<NDCPacket, ClientCallbackContext> dataReadCallback = EMPTY_BICONSUMER(NDCPacket.class, ClientCallbackContext.class);

    private Consumer<ClientCallbackContext> connectInActiveCallback = EMPTY_CONSUMER(ClientCallbackContext.class);


    /*------重试复用------*/
    private List<NDCPacket> authRegisterServices = new CopyOnWriteArrayList<>();


    public void printConfiguration() {
        log.info("启动客户端使用配置：IP:{},端口:{},超时:{}秒", serverHost, serverPort, reconnectInterval);
    }

    public boolean reconnectThisTime() {
        if (autoReconnect) {
            if (reconnectTimes == null) {
                reconnectTimes = 0;
            }

            if (reconnectMaxTimes == -1 || reconnectTimes < reconnectMaxTimes) {
                reconnectTimes++;
                return true;
            }
        }
        return false;
    }

    @Override
    public void check() {
        if (serverHost == null || serverHost.isEmpty()) {
            throw new IllegalArgumentException("host不能为空");
        }
        if (serverPort <= 0) {
            throw new IllegalArgumentException("port必须大于0");
        }

        if (uniqueId == null || uniqueId.isEmpty()) {
            throw new IllegalArgumentException("uniqueId不能为空");
        }

        if (startedCallback == null) {
            throw new IllegalArgumentException("startedCallback不能为空");
        }

        if (startFailCallback == null) {
            throw new IllegalArgumentException("failCallback不能为空");
        }

        if (reconnectInterval == null || reconnectInterval <= 0) {
            throw new IllegalArgumentException("timeoutSecond必须大于0");
        }

        if ((reconnectMaxTimes == null || reconnectMaxTimes < 0) && reconnectMaxTimes != -1) {
            throw new IllegalArgumentException("reconnectLimit必须大于0");
        }

        if (autoReconnect == null) {
            throw new IllegalArgumentException("autoReconnect不能为空");
        }
    }

    public void resetRetryBreak() {
        retryBreak = false;
        waitingThread = null;
    }

    public void doBreakOperation() {
        retryBreak = true;
        if (waitingThread != null) {
            waitingThread.interrupt();
        }
    }

    public void removeRegisterService(NDCPacket ndcPacket) {
        authRegisterServices = authRegisterServices.parallelStream()
                .filter(x -> {
                    LocalService service = x.getObject(LocalService.class);
                    LocalService type = ndcPacket.getObject(LocalService.class);
                    return !service.getServiceId().equals(type.getServiceId());
                }).collect(Collectors.toList());
    }

    public void distinctAddRegisterService(NDCPacket ndcPacket) {
        List<NDCPacket> collect = authRegisterServices
                .parallelStream()
                .filter(tobeSend -> {
                    //是否是服务注册消息
                    if (!NDCPacketHelper.isServiceRegisterPacket(tobeSend)) {
                        return false;
                    }

                    LocalService service = tobeSend.getObject(LocalService.class);
                    LocalService type = ndcPacket.getObject(LocalService.class);
                    return service.getServiceId().equals(type.getServiceId());
                }).collect(Collectors.toList());

        if (collect.isEmpty()) {
            authRegisterServices.add(ndcPacket);
        }
    }
}
