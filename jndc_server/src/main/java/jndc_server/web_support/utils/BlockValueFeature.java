package jndc_server.web_support.utils;


public class BlockValueFeature<T> {
    private T data;
    private Thread blockThread;

    public void complete(T t) {
        data = t;
        synchronized (blockThread) {
            blockThread.notify();
        }
    }

    public T get(Integer second) {
        blockThread = Thread.currentThread();
        synchronized (blockThread) {
            try {
                blockThread.wait(second * 1000);
                return data;
            } catch (InterruptedException e) {
                throw new RuntimeException("get value fail cause " + e);
            }
        }
    }

    public T get() {
        return get(Integer.MAX_VALUE);
    }

}
