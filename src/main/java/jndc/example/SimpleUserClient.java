package jndc.example;



import jndc.utils.LogPrint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class SimpleUserClient {
    public static void main(String[] args)  throws Exception {


        new SimpleUserClient().test2();

//        Socket socket = new Socket("cnigcc.cn", 80);
//        OutputStream outputStream = socket.getOutputStream();
//        String s = "GET / HTTP/1.1\n" +
//                "Host: cnigcc.cn\n" +
//                "Connection: keep-alive\n" +
//                "\r\n";
//        outputStream.write(s.getBytes());
//        InputStream inputStream = socket.getInputStream();
//        byte[] bytes = new byte[1024 * 1024];
//        int read = inputStream.read(bytes);
//        byte[] bytes1 = Arrays.copyOfRange(bytes, 0, read);
//        new String(bytes1);

    }

    public void test2() throws IOException {
        Socket socket = new Socket("123.207.114.245", 83);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("hi".getBytes());



    }


}
