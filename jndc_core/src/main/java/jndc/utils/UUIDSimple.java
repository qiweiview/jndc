package jndc.utils;

import java.util.UUID;

public class UUIDSimple {
    public static String id(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
