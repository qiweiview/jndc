package com.view.jndc.server.config.aspect;


import com.view.jndc.server.model.admin.PureUserEntity;

public class AuthContext {
    private static ThreadLocal<PureUserEntity> authenticated = new ThreadLocal<>();

    public static PureUserEntity getAuthenticated() {
        return authenticated.get();
    }

    public static void setAuthenticated(PureUserEntity value) {
        authenticated.set(value);
    }

    public static void clear() {
        authenticated.remove();
    }
}
