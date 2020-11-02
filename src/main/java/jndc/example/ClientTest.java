package jndc.example;


import jndc.client.JNDCClient;
import jndc.utils.InetUtils;

import java.util.concurrent.TimeUnit;

public class ClientTest {


    public static void main(String[] args) {
        JNDCClient clientTest = new JNDCClient(InetUtils.localTestInetScoket);
        clientTest.createClient();



    }


}
