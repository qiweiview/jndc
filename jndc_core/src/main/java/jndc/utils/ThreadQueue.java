package jndc.utils;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * thread safe
 */
public class ThreadQueue {
    private String name;//队列名
    private LinkedBlockingQueue<InnerTask> linkedBlockingQueue = new LinkedBlockingQueue();//任务队列
    private LinkedBlockingQueue<InnerTask> deathQueue = new LinkedBlockingQueue();//死信队列
    private ExecutorService executorService;//线程池
    private final ThreadLocal<InnerTask> threadLocal = new ThreadLocal();//线程变量
    private Thread worker;//启动线程
    private ThreadQueue nextFailThreadQueue;//下一级失败队列
    private ThreadQueue successLogThreadQueue;//日志队列
    private volatile boolean init = false;//初始化标志
    private ProxyAction proxyAction;
    private final int DEFAULT_FAIL_TIMES=3;

    public ThreadQueue() {
    }


    public ThreadQueue(String name) {
        this.name = name;
    }


    /**
     * 若不为空，过重试次数的任务将被放入队列
     *
     * @param nextFailThreadQueue
     */
    public void setNextFailThreadQueue(ThreadQueue nextFailThreadQueue) {
        this.nextFailThreadQueue = nextFailThreadQueue;
    }

    public String getName() {
        return name;
    }

    /**
     * 初始化组件
     */
    private void start() {
        if (executorService == null) {
            initDefaultPool();
        }

        final ThreadQueue threadQueue = this;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        InnerTask take = linkedBlockingQueue.take();
                        take.register(threadQueue);
                        if (proxyAction != null) {
                            proxyAction.run(take);
                        } else {
                            executorService.execute(take);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        };
        worker = new Thread(runnable);
        worker.start();


    }

    public void submit(Runnable runnable) {
        InnerTask innerTask = new InnerTask() {
            @Override
            public void runDetail() {
                runnable.run();
            }

            @Override
            public Integer configTakRunFailTimes() {
                return DEFAULT_FAIL_TIMES;
            }

            @Override
            public String uniqueTag() {
                return UUID.randomUUID().toString();
            }
        };
        submit(innerTask);
    }

    /**
     * 提交任务
     *
     * @param innerTask
     */
    public void submit(InnerTask innerTask) {
        if (!init) {
            synchronized (this) {
                if (!init) {
                    start();
                    init = true;
                }
            }

        }
        linkedBlockingQueue.add(innerTask);
    }

    /**
     * 死信队列长度
     *
     * @return
     */
    public Integer getFailNumber() {
        return deathQueue.size();
    }

    /**
     * 初始化默认线程池
     */
    private void initDefaultPool() {
        executorService = Executors.newFixedThreadPool(1, new InnerThreadFactory());
    }


    /**
     * 代理执行，不为空时任务将交由代理执行器执行
     *
     * @param proxyAction
     */
    public void setProxyAction(ProxyAction proxyAction) {
        this.proxyAction = proxyAction;
    }

    /**
     * 任务执行成功通知队列，不为空时，任务执行结束后将发送给通知队列
     *
     * @param successLogThreadQueue
     */
    public void setSuccessLogThreadQueue(ThreadQueue successLogThreadQueue) {
        this.successLogThreadQueue = successLogThreadQueue;
    }

    /**
     * 返回死信队列
     * @return
     */
    public LinkedBlockingQueue<InnerTask> getDeathQueue() {
        return deathQueue;
    }

    /**
     * 线程工厂
     */
    private class InnerThreadFactory implements ThreadFactory {


        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    e.printStackTrace();
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    InnerTask innerTask = threadLocal.get();
                    if (innerTask.runBreak()) {
                        if (nextFailThreadQueue != null) {
                            innerTask.clearRecord();//清空错误计数器
                            nextFailThreadQueue.submit(innerTask);
                        } else {
                            //进入死信队列，不再做任何操作
                            deathQueue.add(innerTask);
                        }

                    } else {
                        linkedBlockingQueue.add(innerTask);
                    }
                }
            };
            thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            return thread;
        }
    }

    /**
     * 队列任务
     */
    public static abstract class InnerTask implements Runnable {


        public ThreadQueue threadQueue;
        private Integer failTime = 0;


        private void register(ThreadQueue threadQueue) {
            this.threadQueue = threadQueue;
        }

        public String getUniqueKey() {
            return UUID.randomUUID().toString();
        }

        public void clearRecord() {
            this.failTime = 0;
        }

        public boolean runBreak() {
            failTime++;
            return failTime >= configTakRunFailTimes();
        }

        /**
         * 实际业务代码执行
         */
        public abstract void runDetail();

        /**
         * 失败次数，如返回1，则将在第一次失败就不再尝试
         * @return
         */
        public abstract Integer configTakRunFailTimes();

        /**
         * 预留唯一标志
         * @return
         */
        public abstract String uniqueTag();

        @Override
        public void run() {
            threadQueue.threadLocal.set(this);
            runDetail();
            if (threadQueue.successLogThreadQueue != null) {
                //todo 如果执行完成通知队列存在
                threadQueue.successLogThreadQueue.submit(this);
            }
        }
    }


    /**
     * 代理执行器
     */
    public interface ProxyAction {
        public void run(InnerTask innerTask);
    }
}