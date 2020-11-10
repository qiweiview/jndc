package web.core;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import jndc.core.UniqueBeanManage;
import web.utils.HttpResponseBuilder;

import java.io.File;
import java.util.regex.Matcher;


public class WebContentHandler extends SimpleChannelInboundHandler<JNDCHttpRequest> {
    public static String NAME = "WEB_CONTENT_HANDLER";
    private static final String SEPARATOR = Matcher.quoteReplacement(File.separator);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JNDCHttpRequest jndcHttpRequest) throws Exception {
        if (HttpMethod.GET.equals(jndcHttpRequest.getMethod())) {
            //todo get

            StringBuilder fullPath = jndcHttpRequest.getFullPath();
            String s = fullPath.toString().replaceAll("/", SEPARATOR);

            //jndc inner front project
            FrontProjectLoader jndcStaticProject = FrontProjectLoader.jndcStaticProject;
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


        if (HttpMethod.POST.equals(jndcHttpRequest.getMethod())) {
            //todo post
            MappingRegisterCenter mappingRegisterCenter = UniqueBeanManage.getBean(MappingRegisterCenter.class);
            byte[] data = mappingRegisterCenter.invokeMapping(jndcHttpRequest);
            FullHttpResponse fullHttpResponse;
            if (data == null) {
                fullHttpResponse = HttpResponseBuilder.notFoundResponse();
            } else {
                fullHttpResponse = HttpResponseBuilder.jsonResponse(data);

                //configuration during development
                fullHttpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
                fullHttpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"POST");
                fullHttpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
            }
            channelHandlerContext.writeAndFlush(fullHttpResponse);
            return;
        }

        if (HttpMethod.OPTIONS.equals(jndcHttpRequest.getMethod())) {
            //todo post
            FullHttpResponse fullHttpResponse = HttpResponseBuilder.emptyResponse();

            //configuration during development
            fullHttpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
            fullHttpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"POST");
            fullHttpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
            channelHandlerContext.writeAndFlush(fullHttpResponse);
            return;
        }

        FullHttpResponse fullHttpResponse = HttpResponseBuilder.emptyResponse();
        fullHttpResponse.setStatus(HttpResponseStatus.BAD_REQUEST);
        channelHandlerContext.writeAndFlush(fullHttpResponse);

    }


}
