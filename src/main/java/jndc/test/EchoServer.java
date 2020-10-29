package jndc.test;


import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    public static void main(String[] args)  throws Exception {

        ServerSocket socket = new ServerSocket(888);
        while (true){
            Socket accept = socket.accept();
            OutputStream outputStream = accept.getOutputStream();
            outputStream.write("hello word".getBytes());
            outputStream.flush();
        }


    }


}
