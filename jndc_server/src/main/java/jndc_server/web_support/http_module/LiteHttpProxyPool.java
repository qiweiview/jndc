package jndc_server.web_support.http_module;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
public class LiteHttpProxyPool {

    private static final Integer LIMIT = 10;

    private static AtomicInteger blockNum = new AtomicInteger(0);

    private static BlockingQueue<LiteHttpProxy> blockingQueue = new LinkedBlockingQueue<>();


    private static final Consumer<LiteHttpProxy> tConsumer = (x) -> {
        try {
            blockingQueue.put(x);
        } catch (InterruptedException e) {
            throw new RuntimeException("获取请求器异常" + e);
        }
    };

    static {
        //5个请求器对象
        IntStream.generate(() -> 1).limit(10).forEach(x -> {
            try {
                blockingQueue.put(new LiteHttpProxy(tConsumer, true));
            } catch (InterruptedException e) {
                throw new RuntimeException("获取请求器异常" + e);
            }
        });
    }


    /**
     * 获取请求客户端
     *
     * @return
     */
    public static LiteHttpProxy getLiteHttpProxy() {
        try {
            blockCheck(blockNum.incrementAndGet());
            LiteHttpProxy take = blockingQueue.take();
            blockNum.decrementAndGet();
            return take;
        } catch (InterruptedException e) {
            throw new RuntimeException("获取请求器异常" + e);
        }
    }

    /**
     * 阻塞检查
     *
     * @param i
     */
    public static void blockCheck(int i) {
        if (i > LIMIT) {
            long v = (long) (i * 0.5);
            log.info("线程阻塞数量：" + i + ",执行扩容，添加" + v);
            //todo 增加不可回收工作者
            IntStream.generate(() -> 1).limit(v).forEach(x -> {
                try {
                    blockingQueue.put(new LiteHttpProxy(tConsumer, false));
                } catch (InterruptedException e) {
                    throw new RuntimeException("获取请求器异常" + e);
                }
            });
        }
    }
}
