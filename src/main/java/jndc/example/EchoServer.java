package jndc.example;


import jndc.port_redirect.RedirectApp;
import jndc.utils.LogPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class EchoServer {
    private  static final Logger logger = LoggerFactory.getLogger(RedirectApp.class);
    public static final Integer SERVER_PORT=888;
    public static void main(String[] args)  throws Exception {

        ServerSocket socket = new ServerSocket(SERVER_PORT);
        logger.debug("start echo server");
        while (true){
            Socket accept = socket.accept();
            OutputStream outputStream = accept.getOutputStream();
          new Thread(()->{
              while (true){
                  try {
                      outputStream.write("hello word".getBytes());
                      outputStream.flush();
                      TimeUnit.SECONDS.sleep(5);
                  } catch (IOException | InterruptedException e) {
                      e.printStackTrace();
                  }
              }

          }).start();
        }


    }


}
