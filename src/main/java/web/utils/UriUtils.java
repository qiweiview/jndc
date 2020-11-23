package web.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UriUtils {


    public static ParseResult parseUri(String uri) {
        ParseResult parseResult = new ParseResult();
        parseResult.setFullUri(uri);

        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = uri.toCharArray();

        boolean validString = false;
        String key="";
        String value;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '?') {
                parseResult.setReduceUri(new String(Arrays.copyOfRange(chars,0,i)));
                validString=true;
                continue;
            }
            if (!validString){
                continue;
            }

            if (chars[i] == '='&&"".equals(key)) {
                key=stringBuilder.toString();
                stringBuilder.setLength(0);
            } else if (chars[i] == '&') {
                if ("".equals(key)){
                    stringBuilder.setLength(0);
                    continue;
                }
                value=stringBuilder.toString();
                stringBuilder.setLength(0);
                parseResult.put(key,value);
            } else {
                stringBuilder.append(chars[i]);
            }
        }

        if (stringBuilder.length()>0){
            value=stringBuilder.toString();
            parseResult.put(key,value);
        }
        return parseResult;
    }

    public static class ParseResult{
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

        public void put(String k, String v){
           queryMap.put(k,v);
       }
    }
}
