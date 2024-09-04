package com.view.core.protocol;

import com.view.core.server.tcp.ByteServerHandler;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.*;

@Data
public class DataSwapPool {
    private Map<String, ByteServerHandler> registerMap = new ConcurrentHashMap<>();

    private BlockingQueue<NDCPacket> blockingQueue = new LinkedBlockingQueue();

    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public void register(String clientId, ByteServerHandler byteServerHandler) {
        registerMap.put(clientId, byteServerHandler);
    }

    public void unRegister(String clientId) {
        registerMap.remove(clientId);
    }

    public void put(NDCPacket ndcPacket) {
        blockingQueue.offer(ndcPacket);
    }

    //线程池处理队列
    public void start() {
        executorService.submit(() -> {
            while (true) {
                try {
                    NDCPacket ndcPacket = blockingQueue.take();
                    ndcPacket
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
