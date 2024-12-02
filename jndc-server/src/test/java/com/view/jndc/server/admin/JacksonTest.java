package com.view.jndc.server.admin;

import com.view.jndc.server.model.admin.PureUserEntity;
import com.view.jndc.server.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class JacksonTest {

    @Test
    public void test() {
        PureUserEntity pureUserEntity = new PureUserEntity();
        pureUserEntity.init();
        String json = Jackson.toJson(pureUserEntity);
        log.info(json);


    }
}
