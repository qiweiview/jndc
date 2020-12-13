package jndc_server.core;

import jndc.utils.ThreadQueue;

public class AsynchronousEventCenter {


    private static ThreadQueue dbAsynchronousEventQueue;
    private static ThreadQueue systemRunningEventQueue;

    static {
        dbAsynchronousEventQueue=new ThreadQueue("DB_ASYNC_QUEUE");
        systemRunningEventQueue =new ThreadQueue("ANALYSE_QUEUE");
    }



    public void dbJob(Runnable runnable){
        dbAsynchronousEventQueue.submit(runnable);
    }

    public void systemRunningJob(Runnable runnable){
        systemRunningEventQueue.submit(runnable);
    }
}
