import java.io.*;
public class test_sh_cr {
    public static void main(String[] args) throws Exception {
        Process p = new ProcessBuilder("/bin/sh").start();
        new Thread(() -> {
            try {
                int exit = p.waitFor();
                System.out.println("exited with " + exit);
            } catch(Exception e){}
        }).start();
        new Thread(() -> {
            try {
                InputStream is = p.getInputStream();
                int c;
                while((c = is.read()) != -1) {
                    System.out.print((char)c);
                }
            } catch(Exception e){}
        }).start();
        new Thread(() -> {
            try {
                InputStream is = p.getErrorStream();
                int c;
                while((c = is.read()) != -1) {
                    System.out.print((char)c);
                }
            } catch(Exception e){}
        }).start();
        Thread.sleep(1000);
        System.out.println("sending ls\\r");
        p.getOutputStream().write("ls\r".getBytes());
        p.getOutputStream().flush();
        Thread.sleep(2000);
        System.out.println("alive? " + p.isAlive());
        p.destroy();
    }
}
