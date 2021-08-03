package jndc.http_support.model;

import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.handler.codec.http.multipart.MixedFileUpload;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Data
@Slf4j
public class NettyRequest {
    private String uri;

    private String host;


    private String contentType;

    private Map<String, Cookie> cookieMap = new HashMap<>();

    private Map<String, String> headerMap = new HashMap<>();

    private Map<String, UrlQueryKV> queryMap = new HashMap<>();

    private List<String> pathList = new ArrayList<>();

    private StringBuilder fullPath = new StringBuilder();

    private NettyRequestBody nettyRequestBody = new NettyRequestBody();


    public static NettyRequest of(FullHttpRequest fullHttpRequest) {

        //解析请求头
        NettyRequest nettyRequest = new NettyRequest();
        HttpHeaders headers = fullHttpRequest.headers();
        headers.forEach(x -> {
            nettyRequest.addHeader(x.getKey().toLowerCase(), x.getValue());
        });
        nettyRequest.parseHeaders();

        //解析location
        String uri = fullHttpRequest.uri();
        nettyRequest.setUri(uri);
        nettyRequest.parseUri();

        //解析请求体
        nettyRequest.parseBody(fullHttpRequest);


        return nettyRequest;

    }

    /**
     * 解析请求体
     *
     * @param fullHttpRequest
     */
    private void parseBody(FullHttpRequest fullHttpRequest) {


        String contentType = getContentType();
        if (contentType == null) {
            return;
        }

        String[] split = contentType.split(";");
        if (split.length < 1) {
            throw new RuntimeException("can not found content type");
        }

        if ("application/json".equals(split[0])) {
            byte[] bytes = ByteBufUtil.getBytes(fullHttpRequest.content());
            nettyRequestBody.addAttributeValue(new String(bytes));

        } else if ("multipart/form-data".equals(split[0]) || "application/x-www-form-urlencoded".equals(split[0])) {

            //解析请求体
            HttpPostRequestDecoder httpPostRequestDecoder = new HttpPostRequestDecoder(fullHttpRequest);
            List<InterfaceHttpData> bodyHttpDatas = httpPostRequestDecoder.getBodyHttpDatas();
            bodyHttpDatas.forEach(x -> {


                InterfaceHttpData.HttpDataType httpDataType = x.getHttpDataType();
                if (httpDataType == InterfaceHttpData.HttpDataType.Attribute) {
                    try {
                        MixedAttribute mixedFileUpload = (MixedAttribute) x;
                        String name = mixedFileUpload.getName();
                        String value = mixedFileUpload.getString();
                        nettyRequestBody.addAttributeValue(name, value);
                    } catch (IOException e) {
                        log.error("get string fail");
                    }
                }

                if (httpDataType == InterfaceHttpData.HttpDataType.FileUpload) {
                    try {
                        MixedFileUpload mixedFileUpload = (MixedFileUpload) x;
                        String name = mixedFileUpload.getName();
                        byte[] bytes = mixedFileUpload.get();
                        String filename = mixedFileUpload.getFilename();
                        FormDataFile formDataFile = new FormDataFile();
                        formDataFile.setFileData(bytes);
                        formDataFile.setFileName(filename);
                        nettyRequestBody.addFileValue(name, formDataFile);
                    } catch (IOException e) {
                        log.error("get string fail");
                    }
                }


            });

            //释放资源
            httpPostRequestDecoder.destroy();

        } else {
            throw new RuntimeException("un support type");
        }


    }

    /**
     * 解析location
     */
    public void parseUri() {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = this.uri.toCharArray();
        UrlQueryKV urlQueryKV = new UrlQueryKV();
        boolean overQuery = false;
        boolean pathEnd = false;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '/' && !pathEnd) {
                if (stringBuilder.length() != 0) {
                    crateLevelPath(stringBuilder);
                }
                stringBuilder.append(chars[i]);
            } else if (chars[i] == '?') {
                crateLevelPath(stringBuilder);
                overQuery = true;
                pathEnd = true;
            } else if (chars[i] == '=') {
                urlQueryKV.setKey(stringBuilder.toString());
                stringBuilder.setLength(0);
            } else if (chars[i] == '&') {
                urlQueryKV.setValue(stringBuilder.toString());
                queryMap.put(urlQueryKV.getKey(), urlQueryKV);
                stringBuilder.setLength(0);
                urlQueryKV = new UrlQueryKV();
            } else {
                stringBuilder.append(chars[i]);
            }
        }
        if (stringBuilder.length() > 0) {
            if (overQuery) {
                if (urlQueryKV.getKey() == null) {
                    urlQueryKV.setKey(stringBuilder.toString());
                } else {
                    urlQueryKV.setValue(stringBuilder.toString());

                }
                queryMap.put(urlQueryKV.getKey(), urlQueryKV);
            } else {
                crateLevelPath(stringBuilder);
            }
        }


    }

    /**
     * 解析请求头
     */
    public void parseHeaders() {
        String host = headerMap.get(HttpHeaderNames.HOST.toString());
        if (host != null) {
            setHost(host);
        }

        String contentType = headerMap.get(HttpHeaderNames.CONTENT_TYPE.toString());
        if (contentType != null) {
            setContentType(contentType);
        }

        String cookie = headerMap.get(HttpHeaderNames.COOKIE.toString());
        if (cookie != null) {
            parseCookies(cookie);
        }

    }


    public void addHeader(String key, String value) {
        headerMap.put(key, value);
    }


    private void parseCookies(String cookie) {
        Set<Cookie> decode = ServerCookieDecoder.STRICT.decode(cookie);
        decode.forEach(x -> {
            cookieMap.put(x.name(), x);
        });
    }

    private void crateLevelPath(StringBuilder stringBuilder) {
        pathList.add(stringBuilder.toString());
        fullPath.append(stringBuilder.toString());
        stringBuilder.setLength(0);
    }


    public void release() {
        nettyRequestBody.release();
        nettyRequestBody = null;
    }
}
