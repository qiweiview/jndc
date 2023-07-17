package jndc.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationExit {


    public static void exit() {
        log.error("应用即将退出...");
        System.exit(1);
    }
}
