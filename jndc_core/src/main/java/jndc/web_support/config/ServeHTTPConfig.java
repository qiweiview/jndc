package jndc.web_support.config;

import jndc.web_support.utils.SslOneWayContextFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * HTTP配置
 */
@Slf4j
@Data
public class ServeHTTPConfig {

    //http 端口
    private int httpPort;

    //使用ssl
    private boolean useSsl;

    //证书地址
    private String jksPath;

    //证书密码
    private String jksPass;


    //404 页面地址
    private String notFoundPage = "The page piu~ ,not found";

    //证书上下文
    private SSLContext serverSSLContext;

    public void initSsl() {
        if (isUseSsl()) {
            //todo 判断是否使用ssl
            try {
                char[] keyStorePass = getJksPass().toCharArray();
                byte[] keyStore = FileUtils.readFileToByteArray(new File(getJksPath()));
                serverSSLContext = SslOneWayContextFactory.getServerContext(new ByteArrayInputStream(keyStore), keyStorePass);
                log.info("open ssl in the web api");
            } catch (Exception e) {
                setUseSsl(false);
                log.error("init ssl context  fail cause:" + e);
            }


        }


    }


}
