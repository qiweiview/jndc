package com.view.core.model.heart_beat;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
public class HeartBeatPack implements Serializable {
    private static final long serialVersionUID = -7503515194593071980L;

    private String ndcClientId;

    private HeartBeatSource source;


    public boolean isForServer() {
        nullWarning();
        return HeartBeatSource.SERVER.equals(source);
    }

    public boolean isForClient() {
        nullWarning();
        return HeartBeatSource.CLIENT.equals(source);
    }


    public void nullWarning() {
        if (source == null) {
            log.warn("source is null");
        }
    }
}
