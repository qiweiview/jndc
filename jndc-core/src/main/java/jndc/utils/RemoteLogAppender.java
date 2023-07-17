package jndc.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;


/**
 * 远程日志记录
 */
public class RemoteLogAppender extends AppenderBase<ILoggingEvent> {
    public static final String NAME = "RemoteLogAppender";


    @Override
    public String getName() {
        return NAME;
    }


    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        //todo 日志发送至远程
        //   System.out.println("RemoteLogAppender:" + iLoggingEvent);

    }


}
