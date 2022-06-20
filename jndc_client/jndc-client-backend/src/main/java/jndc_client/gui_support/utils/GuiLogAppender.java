package jndc_client.gui_support.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * gui 控制台日志输出
 */
public class GuiLogAppender extends AppenderBase<ILoggingEvent> {
    public static final  String NAME="GuiLogAppender";

    public static final LinkedBlockingQueue<ILoggingEvent> deathQueue = new LinkedBlockingQueue();

    public static boolean printIntoGui=false;



    @Override
    public String getName() {
        return NAME;
    }



    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if (printIntoGui){
            deathQueue.add(iLoggingEvent);
        }else {
            System.out.println(iLoggingEvent);
        }

    }

    public LinkedBlockingQueue<ILoggingEvent> getDeathQueue() {
        return deathQueue;
    }
}
