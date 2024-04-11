package com.view.core.utils;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

@Slf4j
public class SSLContextGenerator {


    public static SslContext generateSslContextAuto() {
        // 生成自签名证书
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }


}

