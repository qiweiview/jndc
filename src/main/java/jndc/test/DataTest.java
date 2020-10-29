package jndc.test;


import jndc.utils.LogPrint;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class DataTest {
    public static void main(String[] args)  throws Exception {

        Socket socket = new Socket("localhost", 777);
        OutputStream outputStream = socket.getOutputStream();
        String s = "GET / HTTP/1.1\n" +
                "Host: 127.0.0.1\n" +
                "Connection: keep-alive\n" +
                "\r\n";
        outputStream.write(s.getBytes());
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024 * 1024];
        int read = inputStream.read(bytes);
        byte[] bytes1 = Arrays.copyOfRange(bytes, 0, read);
        LogPrint.log(new String(bytes1));


    }


}
