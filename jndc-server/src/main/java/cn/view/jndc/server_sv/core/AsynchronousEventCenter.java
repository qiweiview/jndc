package cn.view.jndc.server_sv.core;

import jndc.core.NDCMessageProtocol;
import jndc.utils.ThreadQueue;
import jndc_server.core.port_app.ServerPortProtector;
import lombok.Data;

/**
 * 异步事件中心
 */
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

    /**
     * 端口绑定上下文
     */
    @Data
    public static class ServerPortBindContext {
        private int port;

        //0 物理端口 1 虚拟端口
        private int virtualTag;

        //端口监听对象，接收端口所有tcp请求（对外）
        private ServerPortProtector serverPortProtector;

        //端口绑定服务描述（对内）
        private ServerServiceDescription serverServiceDescription;


        public ServerPortBindContext(int port) {
            this.port = port;
        }

        public void releaseRelatedResources() {
            //判断是否为物理端口
            if (isPhysics()) {
                //todo 释放端口监听器
                serverPortProtector.releaseRelatedResources();
            }
        }

        public boolean isPhysics() {
            return getVirtualTag() == 0;
        }


        public void receiveMessage(NDCMessageProtocol ndcMessageProtocol) {
            serverPortProtector.receiveMessage(ndcMessageProtocol);
        }

        public void connectionInterrupt(NDCMessageProtocol ndcMessageProtocol) {
            serverPortProtector.connectionInterrupt(ndcMessageProtocol);
        }
    }
}
