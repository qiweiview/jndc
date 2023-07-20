package cn.view.jndc.server_sv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication(scanBasePackages = {"cn.view.jndc"})
public class JNDCServerApplication {

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Good night everyone ");
        }));

        SpringApplication.run(JNDCServerApplication.class, args);
    }

}
