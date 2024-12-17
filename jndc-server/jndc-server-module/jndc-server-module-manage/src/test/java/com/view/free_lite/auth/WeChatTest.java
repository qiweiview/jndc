package com.view.free_lite.auth;

import com.view.free_lite.auth.config.WeChatAPI;
import com.view.free_lite.common.config.cache.JMemoryCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class WeChatTest {

    @Test
    public void test() {
        String code = "0191b4a90680e217ddaa197942a7273404892a5863df24364582720844c49e45";
        String appId = "wx55bd507c852abae2";
        String appSecret = "ca65d4be7a679430b59d975ac6a6090e";
        JMemoryCache jMemoryCache = new JMemoryCache();
        WeChatAPI weChatAPI = new WeChatAPI(jMemoryCache);
        String appletsAccessToken = weChatAPI.getAppletsAccessToken(appId, appSecret);
        String userPhoneNumber = weChatAPI.getUserPhoneNumber(appletsAccessToken, code);
        log.info("userPhoneNumber:{}", userPhoneNumber);
    }
}
