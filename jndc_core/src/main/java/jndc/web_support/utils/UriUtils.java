package jndc.web_support.utils;

import jndc.utils.StringUtils4V;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UriUtils {


    public static ParseResult parseUri(String uri) {
        ParseResult parseResult = new ParseResult();
        parseResult.setFullUri(uri);
        int queryIndex = uri.indexOf('?');
        if (queryIndex < 0) {
            parseResult.setReduceUri(uri);
            return parseResult;
        }

        parseResult.setReduceUri(uri.substring(0, queryIndex));
        String queryString = uri.substring(queryIndex + 1);
        if (StringUtils4V.isBlank(queryString)) {
            return parseResult;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            if (StringUtils4V.isBlank(pair)) {
                continue;
            }

            int equalIndex = pair.indexOf('=');
            if (equalIndex < 0) {
                parseResult.put(decodeQueryPart(pair), "");
                continue;
            }

            String key = pair.substring(0, equalIndex);
            String value = pair.substring(equalIndex + 1);
            parseResult.put(decodeQueryPart(key), decodeQueryPart(value));
        }
        return parseResult;
    }

    private static String decodeQueryPart(String value) {
        if (StringUtils4V.isBlank(value)) {
            return value;
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("decode query failed", e);
        }
    }

    public static String normalizeRequestPath(String path) {
        if (StringUtils4V.isBlank(path)) {
            return path;
        }
        if ("/api".equals(path)) {
            return "/";
        }
        if (path.startsWith("/api/")) {
            return path.substring(4);
        }
        return path;
    }

    public static class ParseResult {
        private Map<String, String> queryMap = new HashMap<>();
        private String reduceUri;
        private String fullUri;

        public String getFullUri() {
            return fullUri;
        }

        public void setFullUri(String fullUri) {
            this.fullUri = fullUri;
        }

        public String getReduceUri() {
            return reduceUri;
        }

        public void setReduceUri(String reduceUri) {
            this.reduceUri = reduceUri;
        }

        public Map<String, String> getQueryMap() {
            return queryMap;
        }

        public void setQueryMap(Map<String, String> queryMap) {
            this.queryMap = queryMap;
        }

        public void put(String k, String v) {
            queryMap.put(k, v);
        }
    }
}
