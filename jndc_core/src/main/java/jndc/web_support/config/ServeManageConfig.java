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
public class ServeManageConfig {

    //管理页面api 端口
    private int managementApiPort;

    //登录用户名
    private String loginName;

    //登录密码
    private String loginPassWord;

    //使用ssl
    private boolean useSsl;

    //证书地址
    private String jksPath;

    //证书密码
    private String jksPass;

    //证书上下文
    private SSLContext serverSSLContext;

    //是否扫描目录
    private boolean adminEnable = false;


    private String adminProjectPath;


    public void initSsl() {
        if (isUseSsl()) {
            try {
                try {
                    char[] keyStorePass = getJksPass().toCharArray();
                    byte[] keyStore = FileUtils.readFileToByteArray(new File(getJksPath()));
                    serverSSLContext = SslOneWayContextFactory.getServerContext(new ByteArrayInputStream(keyStore), keyStorePass);
                    log.info("open ssl in the manage api");
                } catch (Exception e) {
                    setUseSsl(false);
                    log.error("load ssl context  fail cause:" + e);
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                setUseSsl(false);
                log.error("init ssl context  fail cause:" + e);
            }


        }


    }
}
