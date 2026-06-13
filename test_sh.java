import java.io.*;
public class test_sh {
    public static void main(String[] args) throws Exception {
        Process p = new ProcessBuilder("/bin/sh").start();
        new Thread(() -> {
            try {
                int exit = p.waitFor();
                System.out.println("exited with " + exit);
            } catch(Exception e){}
        }).start();
        Thread.sleep(2000);
        System.out.println("alive? " + p.isAlive());
        p.getOutputStream().write("echo hello\n".getBytes());
        p.getOutputStream().flush();
        Thread.sleep(1000);
        p.destroy();
    }
}
