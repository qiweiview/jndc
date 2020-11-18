package web.core;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import jndc.core.NDCMessageProtocol;
import jndc.utils.LogPrint;
import web.utils.AuthUtils;
import web.utils.HttpResponseBuilder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AuthTokenChecker extends SimpleChannelInboundHandler<JNDCHttpRequest> {

    public static String NAME = "AUTH_TOKEN_HANDLER";

    private static final Set<String> releaseSet = new HashSet<>();

    private static final Map<String, Long> tokenCacheMap = new ConcurrentHashMap<>();

    static {
        releaseSet.add("/login");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JNDCHttpRequest jndcHttpRequest) throws Exception {


        HttpMethod method = jndcHttpRequest.getMethod();

        String uri = jndcHttpRequest.getUri();
        if (HttpMethod.POST == method) {
            //check auth when post
            if (releaseSet.contains(uri)) {
                //free set
                channelHandlerContext.fireChannelRead(jndcHttpRequest);
                return;
            }

            String stringHeader = jndcHttpRequest.getStringHeader(AsciiString.of("auth-token"));
            if (stringHeader == null) {
                throw new RuntimeException("凭证缺失");
            } else {
                try {

                    //check by cache
                    Long aLong1 = tokenCacheMap.get(stringHeader);
                    if (aLong1 != null) {
                        if (aLong1 > System.currentTimeMillis()) {
                            LogPrint.info("check by cache");
                            channelHandlerContext.fireChannelRead(jndcHttpRequest);
                        } else {
                            tokenCacheMap.remove(stringHeader);
                        }
                    }


                    //token decode
                    byte[] bytes = AuthUtils.webAuthTokenDecode(stringHeader);


                    //check date
                    byte[] bytes1 = Arrays.copyOfRange(bytes, 0, 8);
                    ByteBuffer wrap = ByteBuffer.wrap(bytes1);
                    long aLong = wrap.getLong();
                    if (aLong < System.currentTimeMillis()) {
                        throw new RuntimeException("凭证过期");
                    }

                    //check address
                    byte[] bytes2 = Arrays.copyOfRange(bytes, 8, bytes.length);
                    InetAddress remoteAddress = jndcHttpRequest.getRemoteAddress();
                    byte[] address = remoteAddress.getAddress();
                    if (Arrays.equals(bytes2, address)) {
                        tokenCacheMap.put(stringHeader, aLong);
                        channelHandlerContext.fireChannelRead(jndcHttpRequest);
                    } else {
                        throw new RuntimeException("凭证失效");
                    }


                } catch (Exception e) {
                    throw new RuntimeException("凭证解签失败");
                }


            }

        } else {
            channelHandlerContext.fireChannelRead(jndcHttpRequest);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FullHttpResponse fullHttpResponse = HttpResponseBuilder.textResponse(cause.getMessage().getBytes());
        fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
        ctx.writeAndFlush(fullHttpResponse).addListeners(ChannelFutureListener.CLOSE);
    }

}
