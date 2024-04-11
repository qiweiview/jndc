package com.view.core.server.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class CustomerHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (request.method() == HttpMethod.POST) {
            //todo 处理POST请求
            handlePost(ctx, request);
        } else if (request.method() == HttpMethod.GET) {
            //todo 处理GET请求
            handleGet(ctx, request);
        } else if (request.method() == HttpMethod.PUT || request.method() == HttpMethod.DELETE) {
            //todo 处理PUT、DELETE等请求
            handlePutDelete(ctx, request);
        } else {
            //todo 其他请求
            ctx.writeAndFlush(DefaultFullHttpResponse.EMPTY_LAST_CONTENT)
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 处理PUT、DELETE等请求
     *
     * @param ctx
     * @param request
     */
    private void handlePutDelete(ChannelHandlerContext ctx, FullHttpRequest request) {
    }

    /**
     * 处理GET请求
     *
     * @param ctx
     * @param request
     */
    private void handleGet(ChannelHandlerContext ctx, FullHttpRequest request) {

        AtomicBoolean breakFlag = new AtomicBoolean(false);
        //解析url参数
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        decoder.parameters().forEach((key, value) -> {
            log.info("Found parameter: " + key + "=" + value);

            if ("forward".equals(key)) {
                //重定向
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
                response.headers().set(HttpHeaderNames.LOCATION, value);
                ctx.writeAndFlush(response)
                        .addListener(ChannelFutureListener.CLOSE);
                breakFlag.set(true);
            }

        });

        if (breakFlag.get()) {
            return;
        }


        // 构造响应内容
        ByteBuf content = Unpooled.copiedBuffer("Get handle finished!", CharsetUtil.UTF_8);

        // 构造响应对象
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

        // 设置响应头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

        // 发送响应
        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 处理POST请求
     *
     * @param ctx
     * @param request
     */
    private void handlePost(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (request.headers().contains(HttpHeaderNames.CONTENT_TYPE) && request.headers().get(HttpHeaderNames.CONTENT_TYPE).startsWith("application/json")) {
            //todo 处理json数据
            ByteBuf content = request.content();
            log.info("Found json data: " + content.toString(CharsetUtil.UTF_8));
        } else {
            //todo 处理表单数据
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);

            //提取请求体
            decoder.offer(request);

            //遍历请求体
            decoder.getBodyHttpDatas().forEach(httpData -> {


                if (httpData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    //todo 参数
                    Attribute attribute = (Attribute) httpData;
                    try {
                        log.info("Found attribute: " + attribute.getName() + "=" + attribute.getValue());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // 处理普通文本数据
                } else if (httpData.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    //todo 文件
                    FileUpload fileUpload = (FileUpload) httpData;
                    try {
                        byte[] bytes = fileUpload.get();
                        log.info("Found file: " + fileUpload.getFilename() + "content:" + new String(bytes));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

        }



        // 构造响应内容
        ByteBuf content = Unpooled.copiedBuffer("Post handle finished!", CharsetUtil.UTF_8);

        // 构造响应对象
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

        // 设置响应头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

        // 发送响应
        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常：", cause);
    }
}