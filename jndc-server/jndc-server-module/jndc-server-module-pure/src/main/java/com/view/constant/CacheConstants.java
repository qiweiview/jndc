package com.view.constant;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-04 16:10
 * @description: TODO
 */
public class CacheConstants {

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";
}
