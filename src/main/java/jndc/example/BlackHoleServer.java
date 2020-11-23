package jndc.example;

import jndc.port_redirect.RedirectApp;
import jndc.utils.LogPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.InputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


/**
 * print all data send to this port
 */
public class BlackHoleServer {

    private  static final Logger logger = LoggerFactory.getLogger(RedirectApp.class);
    public static final Integer SERVER_PORT=888;
    public static void main(String[] args)  throws Exception {

        ServerSocket socket = new ServerSocket(SERVER_PORT);
        logger.debug("start echo server");
        while (true){
            Socket accept = socket.accept();
            InputStream inputStream = accept.getInputStream();
            byte[] bytes=new byte[10*1024*1024];
            int read = inputStream.read(bytes);
            byte[] bytes1 = Arrays.copyOfRange(bytes, 0, read);
            LogPrint.info(new String(bytes1));
            accept.close();
        }


    }

}
