package com.view.core.utils;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.util.Date;

import java.security.SecureRandom;
import java.math.BigInteger;
import javax.net.ssl.SSLException;

@Slf4j
public class SSLContextGenerator {

    public static final SslContext SSL_CONTEXT = generateSslContextAutoSimple();


    public static SslContext generateSslContextAutoSimple() {
        // 生成自签名证书
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } catch ( Exception e) {
            log.error("Failed to generate SSL context", e);
            throw new RuntimeException("Failed to generate SSL context", e);
        }
    }

    public static SslContext generateSslContextAuto() {
        try {
            // 生成密钥对
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 创建自签名证书
            X509Certificate certificate = generateCertificate(keyPair);

            PrivateKey aPrivate = keyPair.getPrivate();

            // 构建 SslContext
            return SslContextBuilder.forServer(aPrivate,certificate ).build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SSL context", e);
        }
    }


    public static X509Certificate generateCertificate( KeyPair keyPair ) throws Exception {
        // 1. 生成密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);


        // 2. 创建证书颁发者的名称（一般是"CN=example"等）
        X500Name issuer = new X500Name("CN=exampleIssuer");

        // 3. 创建证书的主题（一般是"CN=subject"等）
        X500Name subject = new X500Name("CN=exampleSubject");

        // 4. 使用X509v3CertificateBuilder构建证书
        Date notBefore = new Date();
        Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000); // 1年有效期
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis()); // 序列号

        // 将公钥转换为SubjectPublicKeyInfo
        PublicKey publicKey = keyPair.getPublic();
        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(
                issuer, serial, notBefore, notAfter, subject, subjectPublicKeyInfo);

        // 5. 签名证书
        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());

        // 使用JcaX509CertificateConverter将构建的证书转换为X509Certificate
        X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(certificateBuilder.build(contentSigner));

        // 返回生成的证书
        return certificate;
    }

}

