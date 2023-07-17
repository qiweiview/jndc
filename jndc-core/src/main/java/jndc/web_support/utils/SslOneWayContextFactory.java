package jndc.web_support.utils;


import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

public final class SslOneWayContextFactory {

    private static final String PROTOCOL = "TLS";


    public static SSLContext getServerContext(InputStream stream, char[] keyStore) throws Exception {


        //密钥管理器
        KeyStore ks = KeyStore.getInstance("JKS");

        //加载服务端的KeyStore  ；sNetty是生成仓库时设置的密码，用于检查密钥库完整性的密码
        ks.load(stream, keyStore);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        //初始化密钥管理器
        kmf.init(ks, keyStore);

        //获取安全套接字协议（TLS协议）的对象
        SSLContext instance = SSLContext.getInstance(PROTOCOL);

        //初始化此上下文
        //参数一：认证的密钥      参数二：对等信任认证  参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
        instance.init(kmf.getKeyManagers(), null, null);

        if (stream != null) {
            stream.close();
        }
        return instance;
    }

}
