package com.view.free_lite.common.utils;

import java.util.UUID;

public class UniqueId {

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
