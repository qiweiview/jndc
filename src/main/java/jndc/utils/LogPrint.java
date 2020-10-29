package jndc.utils;

public class LogPrint {


    public static  void log(Object msg, Object source){
        if (source!=null){

        }
        System.out.println(msg);
    }

    public static  void log(Object msg){
        log(msg,null);
    }
}
