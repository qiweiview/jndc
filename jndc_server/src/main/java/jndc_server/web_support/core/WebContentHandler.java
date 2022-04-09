package jndc_server.web_support.core;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import jndc.core.UniqueBeanManage;
import jndc_server.web_support.utils.HttpResponseBuilder;
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
            //todo get

            StringBuilder fullPath = jndcHttpRequest.getFullPath();

            String fullPathStr = fullPath.toString();

            if ("/".equals(fullPathStr)){
                fullPathStr="/index.html";
            }

            String s = fullPathStr.replaceAll("/", SEPARATOR);


            //jndc inner front project
            FrontProjectLoader jndcStaticProject = FrontProjectLoader.jndcStaticProject;

            if (jndcStaticProject==null){
                channelHandlerContext.writeAndFlush(HttpResponseBuilder.notFoundResponse());
                return;
            }

            //find static file
            FrontProjectLoader.InnerFileDescription file = jndcStaticProject.findFile(s);
            FullHttpResponse fullHttpResponse;
            if (file == null) {
                fullHttpResponse = HttpResponseBuilder.notFoundResponse();
            } else {
                fullHttpResponse = HttpResponseBuilder.fileResponse(file.getData(), file.getFileType());
            }
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
