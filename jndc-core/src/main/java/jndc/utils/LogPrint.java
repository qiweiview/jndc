package jndc.utils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LogPrint {

    public static  void info(Object msg, Object source){
        if (source!=null){

        }
        if (msg==null){
            msg="";
        }
        log.info(msg.toString());
    }

    public static  void info(Object msg){
        info(msg,null);
    }

    public static  void err(Object msg, Object source){
        if (source!=null){

        }
        if (msg==null){
            msg="";
        }
        log.error(msg.toString());
    }

    public static  void err(Object msg){
        err(msg,null);
    }

    public static void debug(Object msg) {
        log.debug(msg.toString());
    }

    public static  void debug(Object msg, Object source){
        if (source!=null){

        }
        if (msg==null){
            msg="";
        }
        log.debug(msg.toString());
    }

}
