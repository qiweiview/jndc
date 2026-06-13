import java.io.*;
public class test_sh_bs {
    public static void main(String[] args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("/bin/sh");
        pb.redirectErrorStream(true);
        Process p = pb.start();
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
        // send 'l' 's' '<backspace>' '<backspace>' 'p' 'w' 'd' '\n'
        p.getOutputStream().write(new byte[]{'l', 's', 0x7f, 0x7f, 'p', 'w', 'd', '\n'});
        p.getOutputStream().flush();
        Thread.sleep(1000);
        p.destroy();
    }
}
