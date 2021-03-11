package jndc_server.web_support.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockValueFeature<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private T data;
    private Thread blockThread;

    public BlockValueFeature() {
        logger.info("new");
    }

    public void complete(T t) {
        logger.debug("complete by value: "+t);
        data = t;
        synchronized (this) {
            this.notify();
        }
    }

    public T get(Integer second) {
        blockThread = Thread.currentThread();
        synchronized (this) {
            try {
                if (second>0){
                    this.wait(second * 1000);
                }else {
                    this.wait();
                }
                logger.debug("return value: "+data);
                return data;
            } catch (InterruptedException e) {
                throw new RuntimeException("get value fail cause " + e);
            }
        }
    }

    public T get() {
        return get(-1);
    }


}
