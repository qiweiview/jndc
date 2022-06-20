package jndc_server.web_support.http_module;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
public class LiteHttpProxyPool {


    private static final Integer FIX_SIZE = 1;

    private static final Integer INCREASE_STEP = 5;

    private static volatile AtomicInteger freeNum = new AtomicInteger(0);

    private static BlockingQueue<LiteHttpProxy> blockingQueue = new LinkedBlockingQueue<>();


    static {
        //5个请求器对象
        IntStream.generate(() -> 1).limit(FIX_SIZE).forEach(x -> {
            try {
                LiteHttpProxy liteHttpProxy = new LiteHttpProxy(getConsumer(), true);
                liteHttpProxy.setId("INIT_CLIENT");
                blockingQueue.put(liteHttpProxy);
                log.debug("初始化,可用计数可用：" + freeNum.incrementAndGet() + "实际可用：" + blockingQueue.size());
            } catch (InterruptedException e) {
                throw new RuntimeException("获取请求器异常" + e);
            }
        });
    }


    private static Consumer<LiteHttpProxy> getConsumer() {
        Consumer<LiteHttpProxy> tConsumer = (x) -> {
            try {
                if (x.canBeReuse()) {

                    //同步
                    if (x.canBePut()) {
                        synchronized (x) {
                            if (x.canBePut()) {
                                //todo 可重用且未被回收
                                freeNum.incrementAndGet();
                                blockingQueue.put(x);
                                x.putOption();
                                log.debug("客户端回收,当前可用客户端计数：" + freeNum.get() + "实际可用：" + blockingQueue.size());
                            }
                        }
                    }


                }
            } catch (InterruptedException e) {
                throw new RuntimeException("获取请求器异常" + e);
            }
        };
        return tConsumer;
    }

    /**
     * 获取请求客户端
     *
     * @return
     */
    public static LiteHttpProxy getLiteHttpProxy() {
        try {
            blockCheck(freeNum.get());
            LiteHttpProxy take = blockingQueue.take();
            take.takeOption();
            log.debug("客户端使用,当前可用客户端计数：" + freeNum.decrementAndGet() + "实际可用：" + blockingQueue.size());

            return take;
        } catch (InterruptedException e) {
            throw new RuntimeException("获取请求器异常" + e);
        }
    }

    /**
     * 阻塞检查
     *
     * @param free
     */
    public static void blockCheck(int free) {
        if (free == 0) {
            //todo 增加不可回收工作者
            IntStream.generate(() -> 1).limit(INCREASE_STEP).parallel().forEach(x -> {
                try {
                    freeNum.incrementAndGet();
                    blockingQueue.put(new LiteHttpProxy(getConsumer(), false));
                } catch (InterruptedException e) {
                    throw new RuntimeException("扩充请求器异常" + e);
                }
            });
            log.info("扩容后,可用客户端计数：" + freeNum.get() + "实际可用：" + blockingQueue.size());
        }
    }
}
