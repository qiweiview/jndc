package jndc.web_support.core;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import jndc.core.UniqueBeanManage;
import jndc.web_support.utils.HttpResponseBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.regex.Matcher;


/**
 * 管理端api处理器
 */
@Slf4j
public class WebContentHandler extends SimpleChannelInboundHandler<JNDCHttpRequest> {
    public static String NAME = "WEB_CONTENT_HANDLER";
    private static final String SEPARATOR = Matcher.quoteReplacement(File.separator);


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JNDCHttpRequest jndcHttpRequest) throws Exception {

        //get method
        if (HttpMethod.GET.equals(jndcHttpRequest.getMethod())) {
            //todo GET类型请求

            String fullPathStr = jndcHttpRequest.getFullPath();

            if ("/".equals(fullPathStr)){
                fullPathStr="/index.html";
            }

            String s = fullPathStr.replaceAll("/", SEPARATOR);


            //静态资源
            FrontProjectLoader jndcStaticProject = FrontProjectLoader.jndcStaticProject;

            if (jndcStaticProject==null){
                //todo 资源未找到
                channelHandlerContext.writeAndFlush(HttpResponseBuilder.notFoundResponse());
                return;
            }

            //查找静态资源
            FrontProjectLoader.InnerFileDescription file = jndcStaticProject.findFile(s);
            if (file == null) {
                channelHandlerContext.writeAndFlush(HttpResponseBuilder.notFoundResponse());
                return;
            }

            byte[] fileData = file.getData();
            
            // Handle conditional requests
            String ifNoneMatch = jndcHttpRequest.getStringHeader(HttpHeaderNames.IF_NONE_MATCH);
            if (ifNoneMatch != null) {
                String etag = "\"" + Integer.toHexString(fileData.hashCode()) + "\"";
                if (etag.equals(ifNoneMatch)) {
                    // Resource hasn't changed, send 304 Not Modified
                    FullHttpResponse notModifiedResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
                    notModifiedResponse.headers().set(HttpHeaderNames.ETAG, etag);
                    channelHandlerContext.writeAndFlush(notModifiedResponse);
                    return;
                }
            }

            // Send the full response
            FullHttpResponse fullHttpResponse = HttpResponseBuilder.fileResponse(fileData, file.getFileType());
            channelHandlerContext.writeAndFlush(fullHttpResponse);
            return;
        }


        //post method
        if (HttpMethod.POST.equals(jndcHttpRequest.getMethod())) {
            //todo post
            MappingRegisterCenter mappingRegisterCenter = UniqueBeanManage.getBean(MappingRegisterCenter.class);
            byte[] data = mappingRegisterCenter.invokeMapping(jndcHttpRequest);
            FullHttpResponse fullHttpResponse;
            if (data == null) {
                fullHttpResponse = HttpResponseBuilder.notFoundResponse();
            } else {
                //todo 没有对应mapping
                fullHttpResponse = HttpResponseBuilder.jsonResponse(data);
            }
            fullHttpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,auth-token");
            channelHandlerContext.writeAndFlush(fullHttpResponse);
            return;
        }


        //不支持请求类型
        FullHttpResponse fullHttpResponse = HttpResponseBuilder.emptyResponse();
        fullHttpResponse.setStatus(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE);
        channelHandlerContext.writeAndFlush(fullHttpResponse);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("unCatchableException: " + cause);

    }
}
