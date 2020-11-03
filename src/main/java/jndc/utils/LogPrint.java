package jndc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class LogPrint {
    final static Logger logger = LoggerFactory.getLogger(LogPrint.class);

    public static  void log(Object msg, Object source){
        if (source!=null){

        }
        if (msg==null){
            msg="";
        }
        logger.info(msg.toString());
    }

    public static  void log(Object msg){
        log(msg,null);
    }

    public static  void err(Object msg, Object source){
        if (source!=null){

        }
        if (msg==null){
            msg="";
        }
        logger.error(msg.toString());
    }

    public static  void err(Object msg){
        err(msg,null);
    }
}
