package jndc_server.config;

public class ServerRuntimeConfig {
    public static boolean DEBUG_MODEL=false;//only be changed on runtime,init value must be false

    public static boolean deployManagementProject(){
        return !DEBUG_MODEL;
    }
}
