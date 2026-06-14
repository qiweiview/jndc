package jndc_client.core;

import lombok.extern.slf4j.Slf4j;


/**
 * 主服务
 */
@Slf4j
public class JNDClientApp {


    public void createClient() {

        //核心服务
        JNDCClient jndcClient = new JNDCClient();
        jndcClient.start();


    }


}
