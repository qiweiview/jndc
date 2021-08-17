package jndc_server.web_support.http_module;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

public class LiteProxyHandle extends SimpleChannelInboundHandler<FullHttpResponse> {
    public static String NAME = "LITE_PROXY_HANDLE";

    private LiteHttpProxy liteHttpProxy;

    public LiteProxyHandle(LiteHttpProxy liteHttpProxy) {
        this.liteHttpProxy = liteHttpProxy;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpResponse fullHttpResponse) throws Exception {
        if (liteHttpProxy == null) {
            return;
        }
        liteHttpProxy.writeData(fullHttpResponse.retain());

        //http单工，客户端设计成只执行一次就回收
        release();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //客户端目标主动断开
        release();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    public void release() {
        if (liteHttpProxy != null) {
            liteHttpProxy.release();
            liteHttpProxy = null;
        }
    }
}
