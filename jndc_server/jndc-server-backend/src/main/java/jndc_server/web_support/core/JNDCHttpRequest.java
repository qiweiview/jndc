package jndc_server.web_support.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.AsciiString;
import jndc.utils.JSONUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Data
public class JNDCHttpRequest {
    private FullHttpRequest fullHttpRequest;

    private HttpMethod method;

    private InetAddress remoteAddress;

    private HttpHeaders headers;

    private byte[] body;

    private String uri;

    private String hostName;

    private String fullPath;

    private Map<String, QueryKV> queryMap;

    /**
     * 获取对应对象
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getObject(Class<T> tClass) {
        byte[] body = getBody();
        String s = new String(body);
        return JSONUtils.str2Object(s, tClass);
    }

    public JNDCHttpRequest(FullHttpRequest fullHttpRequest) {
        ByteBuf content = fullHttpRequest.content();
        this.body = ByteBufUtil.getBytes(content);
        this.method = fullHttpRequest.method();
        this.queryMap = new HashMap<>();
        this.fullPath = "";
        this.fullHttpRequest = fullHttpRequest;
        this.headers = fullHttpRequest.headers();
        parseRequest();
    }


    private void parseRequest() {
        this.uri = this.fullHttpRequest.uri();
        this.hostName = getStringHeader(HttpHeaderNames.HOST);
        parseUri();

    }


    private void parseUri() {
        try {
            String uri = "http://hi" + this.uri;

            URL url = new URL(uri);
            String query = url.getQuery();
            if (query != null) {
                Stream.of(query.split("&")).forEach(x -> {
                    String[] split = x.split("=");
                    QueryKV queryKV = new QueryKV();
                    if (split.length == 1) {
                        queryKV.setKey(split[0]);
                        queryKV.setValue("");
                        queryMap.put(queryKV.getKey(), queryKV);
                    } else if (split.length > 1) {
                        queryKV.setKey(split[0]);
                        queryKV.setValue(split[1]);
                        queryMap.put(queryKV.getKey(), queryKV);
                    }
                });
            }


            //创建地址
            String path = url.getPath();
            setFullPath(path);
        } catch (MalformedURLException e) {
            log.error("ur解析异常：" + e + "/" + uri);
        }
    }

    public String getStringHeader(AsciiString name) {
        return headers.get(name);
    }

    public Integer getIntHeader(AsciiString name) {
        return headers.getInt(name);
    }

//    private void parseUri() {
//        StringBuilder stringBuilder = new StringBuilder();
//        char[] chars = this.uri.toCharArray();
//        InnerQueryValue innerQueryValue = new InnerQueryValue();
//        boolean overQuery = false;
//        boolean pathEnd = false;
//        for (int i = 0; i < chars.length; i++) {
//            if (chars[i] == '/' && !pathEnd) {
//                if (stringBuilder.length() != 0) {
//                    crateLevelPath(stringBuilder);
//                }
//                stringBuilder.append(chars[i]);
//            } else if (chars[i] == '?') {
//                crateLevelPath(stringBuilder);
//                overQuery = true;
//                pathEnd = true;
//            } else if (chars[i] == '=') {
//                innerQueryValue.setKey(stringBuilder.toString());
//                stringBuilder.setLength(0);
//            } else if (chars[i] == '&') {
//                innerQueryValue.setValue(stringBuilder.toString());
//                queryMap.put(innerQueryValue.getKey(), innerQueryValue);
//                stringBuilder.setLength(0);
//                innerQueryValue = new InnerQueryValue();
//            } else {
//                stringBuilder.append(chars[i]);
//            }
//        }
//        if (stringBuilder.length() > 0) {
//            if (overQuery) {
//                if (innerQueryValue.getKey() == null) {
//                    innerQueryValue.setKey(stringBuilder.toString());
//                } else {
//                    innerQueryValue.setValue(stringBuilder.toString());
//
//                }
//                queryMap.put(innerQueryValue.getKey(), innerQueryValue);
//            } else {
//                crateLevelPath(stringBuilder);
//            }
//        }
//
//
//    }


//    private void crateLevelPath(String  stringBuilder) {
//        pathList.add(stringBuilder );
//        fullPath.append(stringBuilder );
//    }
//
//    private void crateLevelPath(StringBuilder stringBuilder) {
//       crateLevelPath(stringBuilder.toString());
//        stringBuilder.setLength(0);
//    }


}
