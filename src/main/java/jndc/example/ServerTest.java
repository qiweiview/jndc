package jndc.example;

import jndc.server.JNDCServer;

public class ServerTest {
    public static final Integer SERVER_PORT=81;



    public static void main(String[] args) {
        JNDCServer serverTest =new JNDCServer(SERVER_PORT);
        serverTest.createServer();
    }


}
