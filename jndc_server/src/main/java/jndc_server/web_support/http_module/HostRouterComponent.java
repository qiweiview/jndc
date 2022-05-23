package jndc_server.web_support.http_module;

import io.netty.util.internal.StringUtil;
import jndc.core.data_store_support.DBWrapper;
import jndc_server.web_support.model.d_o.HttpHostRoute;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 域名路由
 */
public class HostRouterComponent {
    private final Map<String, HttpHostRoute> map = new ConcurrentHashMap<>();
    private volatile boolean initFinished = false;

    public void addRule(HttpHostRoute newRule) {
        map.put(newRule.getHostKeyWord(), newRule);
    }

    public void removeRule(HttpHostRoute oldRule) {
        map.remove(oldRule.getHostKeyWord());
    }

    public void updateRule(HttpHostRoute oldRule, HttpHostRoute newRule) {
        removeRule(oldRule);
        addRule(newRule);

    }


    public static String parseHost(String host) {
        String[] split = host.split("\\.");
        if (split.length<3){
            return "";
        }

        return split[0];


    }

    public HttpHostRoute matchHost(String host) {
        if (StringUtil.isNullOrEmpty(host)) {
            return null;
        }

        if (!initFinished) {
            init();
        }
        return map.get(parseHost(host));
    }


    public void init() {
        synchronized (HostRouterComponent.class) {
            if (!initFinished) {
                DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);
                List<HttpHostRoute> httpHostRoutes = dbWrapper.listAll();
                httpHostRoutes.forEach(x -> {
                    map.put(x.getHostKeyWord(), x);
                });
                initFinished=true;
            }
        }

    }
}
