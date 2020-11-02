package jndc.example;


import jndc.utils.LogPrint;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    public static final Integer SERVER_PORT=888;
    public static void main(String[] args)  throws Exception {

        ServerSocket socket = new ServerSocket(SERVER_PORT);
        LogPrint.log("start echo server");
        while (true){
            Socket accept = socket.accept();
            OutputStream outputStream = accept.getOutputStream();
            outputStream.write("hello word".getBytes());
            outputStream.flush();
        }


    }


}
