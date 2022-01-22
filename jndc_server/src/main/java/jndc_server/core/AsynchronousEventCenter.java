package jndc_server.core;

import jndc.utils.ThreadQueue;

public class AsynchronousEventCenter {

    //数据库事件运行队列
    private static ThreadQueue dbAsynchronousEventQueue;

    //系统事件运行队列
    private static ThreadQueue systemRunningEventQueue;

    //流量分析队列
    private static ThreadQueue dataFlowAnalyseRunningEventQueue;

    static {
        dbAsynchronousEventQueue = new ThreadQueue("DB_ASYNC_QUEUE");
        systemRunningEventQueue = new ThreadQueue("SYSTEM_QUEUE");
        dataFlowAnalyseRunningEventQueue = new ThreadQueue("ANALYSE_QUEUE");
    }


    public void dbJob(Runnable runnable) {
        dbAsynchronousEventQueue.submit(runnable);
    }

    public void systemRunningJob(Runnable runnable) {
        systemRunningEventQueue.submit(runnable);
    }

    public void dataAnalyseJob(Runnable runnable) {
        dataFlowAnalyseRunningEventQueue.submit(runnable);
    }

}
