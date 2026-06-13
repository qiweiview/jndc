import java.io.*;
public class test_dir {
    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("/bin/sh");
            pb.directory(new File("/does/not/exist/dir"));
            Process p = pb.start();
            System.out.println("started");
            int exit = p.waitFor();
            System.out.println("exited " + exit);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
