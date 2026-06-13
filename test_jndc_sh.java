import java.io.*;
public class test_jndc_sh {
    public static void main(String[] args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("/bin/sh");
        pb.directory(new File("."));
        pb.redirectErrorStream(true);
        Process p = pb.start();
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
        Thread.sleep(1000);
        System.out.println("alive? " + p.isAlive());
        Thread.sleep(2000);
        p.destroy();
    }
}
