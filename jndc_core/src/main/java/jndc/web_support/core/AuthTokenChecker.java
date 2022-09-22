package jndc.web_support.core;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import jndc.web_support.utils.AuthUtils;
import jndc.web_support.utils.HttpResponseBuilder;
import jndc.web_support.utils.UriUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AuthTokenChecker extends SimpleChannelInboundHandler<FullHttpRequest> {


    public static String NAME = "AUTH_TOKEN_HANDLER";

    private static final String AUTH_TOKEN = "auth-token";

    private static final Set<String> releaseSet = new HashSet<>();

    private static final Map<String, Long> tokenCacheMap = new ConcurrentHashMap<>();

    static {
        releaseSet.add("/");
        releaseSet.add("/login");
        releaseSet.add("/getDeviceIp");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        String s = fullHttpRequest.headers().get(HttpHeaderNames.UPGRADE);
        if ("websocket".equals(s)) {
            //websocket
            UriUtils.ParseResult parseResult = UriUtils.parseUri(fullHttpRequest.uri());
            verificationToken(parseResult.getQueryMap().get(AUTH_TOKEN), channelHandlerContext);

            //reset uri
            fullHttpRequest.setUri(parseResult.getReduceUri());

        } else {
            //base http
            HttpMethod method = fullHttpRequest.method();

            String uri = fullHttpRequest.uri();
            if (HttpMethod.POST == method) {
                //check auth when post
                if (releaseSet.contains(uri)) {
                    //free set
                    channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
                    return;
                }

                /*验证密码*/
                String stringHeader = fullHttpRequest.headers().get(AsciiString.of(AUTH_TOKEN));
//                verificationToken(stringHeader, channelHandlerContext);
            }

        }
        channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
    }


    public void verificationToken(String token, ChannelHandlerContext channelHandlerContext) {
        if (token == null) {
            throw new RuntimeException("凭证缺失");
        } else {
            try {

                //check by cache
                Long aLong1 = tokenCacheMap.get(token);
                if (aLong1 != null) {
                    if (aLong1 > System.currentTimeMillis()) {
                        return;
                    } else {
                        //expired
                        tokenCacheMap.remove(token);
                    }
                }


                //token decode
                byte[] bytes = AuthUtils.webAuthTokenDecode(token);


                //check date
                byte[] bytes1 = Arrays.copyOfRange(bytes, 0, 8);
                ByteBuffer wrap = ByteBuffer.wrap(bytes1);
                long aLong = wrap.getLong();
                if (aLong < System.currentTimeMillis()) {
                    throw new RuntimeException("凭证过期");
                }

                //check address
                byte[] bytes2 = Arrays.copyOfRange(bytes, 8, bytes.length);
                InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
                InetAddress remoteAddress = socketAddress.getAddress();
                byte[] address = remoteAddress.getAddress();
                if (Arrays.equals(bytes2, address)) {
                    tokenCacheMap.put(token, aLong);
                } else {
                    throw new RuntimeException("凭证失效");
                }


            } catch (Exception e) {
                throw new RuntimeException("凭证解签失败,cause: ", e);
            }


        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("catch exception in auth check,cause: " + cause);
        FullHttpResponse fullHttpResponse = HttpResponseBuilder.textResponse(cause.getMessage().getBytes());
        fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);

        ctx.writeAndFlush(fullHttpResponse).addListeners(ChannelFutureListener.CLOSE);
    }


}
