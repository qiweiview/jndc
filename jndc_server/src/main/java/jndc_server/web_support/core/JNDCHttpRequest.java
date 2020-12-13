package jndc_server.web_support.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.AsciiString;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JNDCHttpRequest {
    private FullHttpRequest fullHttpRequest;

    private HttpMethod method;

    private InetAddress remoteAddress;

    private HttpHeaders headers;

    private byte[] body;

    private String uri;

    private String hostName;

    private List<String> pathList;

    private StringBuilder fullPath;


    private Map<String, InnerQueryValue> queryMap;


    public JNDCHttpRequest(FullHttpRequest fullHttpRequest) {
        ByteBuf content = fullHttpRequest.content();
        this.body = ByteBufUtil.getBytes(content);
        this.method = fullHttpRequest.method();
        this.pathList = new ArrayList<>();
        this.queryMap = new HashMap<>();
        this.fullPath = new StringBuilder();
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
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = this.uri.toCharArray();
        InnerQueryValue innerQueryValue = new InnerQueryValue();
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
                innerQueryValue.setKey(stringBuilder.toString());
                stringBuilder.setLength(0);
            } else if (chars[i] == '&') {
                innerQueryValue.setValue(stringBuilder.toString());
                queryMap.put(innerQueryValue.getKey(), innerQueryValue);
                stringBuilder.setLength(0);
                innerQueryValue = new InnerQueryValue();
            } else {
                stringBuilder.append(chars[i]);
            }
        }
        if (stringBuilder.length() > 0) {
            if (overQuery) {
                if (innerQueryValue.getKey() == null) {
                    innerQueryValue.setKey(stringBuilder.toString());
                } else {
                    innerQueryValue.setValue(stringBuilder.toString());

                }
                queryMap.put(innerQueryValue.getKey(), innerQueryValue);
            } else {
                crateLevelPath(stringBuilder);
            }
        }


    }


    private void crateLevelPath(StringBuilder stringBuilder) {
        pathList.add(stringBuilder.toString());
        fullPath.append(stringBuilder.toString());
        stringBuilder.setLength(0);
    }


    /*getter setter*/

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public StringBuilder getFullPath() {
        return fullPath;
    }


    public FullHttpRequest getFullHttpRequest() {
        return fullHttpRequest;
    }

    public void setFullHttpRequest(FullHttpRequest fullHttpRequest) {
        this.fullHttpRequest = fullHttpRequest;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getStringHeader(AsciiString name) {
        return headers.get(name);
    }

    public Integer getIntHeader(AsciiString name) {
        return headers.getInt(name);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Map<String, InnerQueryValue> getQueryMap() {
        return queryMap;
    }

    public void setQueryMap(Map<String, InnerQueryValue> queryMap) {
        this.queryMap = queryMap;
    }

    public List<String> getPathList() {
        return pathList;
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
    }


    /*inner class*/

    public class InnerQueryValue {
        private String key;
        private String value;

        @Override
        public String toString() {
            return value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }


    }
}
