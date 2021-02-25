package jndc_server.web_support.http_module;

import io.netty.util.internal.StringUtil;
import jndc.core.data_store_support.DBWrapper;
import jndc_server.web_support.model.data_object.HttpHostRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HostRouter {
    private final Logger logger = LoggerFactory.getLogger(getClass());
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

    public String parseHost(String host) {
        logger.info("accept host:" + host);
        int i = host.lastIndexOf(".");
        if (i != -1) {
            String substring = host.substring(0, i);
            i = substring.lastIndexOf(".");
            if (i != -1) {
                String result = host.substring(0, i);
                return result;
            }
        }
        return "";

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
        synchronized (HostRouter.class) {
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
