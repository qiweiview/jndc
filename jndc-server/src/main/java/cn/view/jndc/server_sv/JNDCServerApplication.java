package cn.view.jndc.server_sv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"cn.view.jndc"})
public class JNDCServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JNDCServerApplication.class, args);
    }

}
