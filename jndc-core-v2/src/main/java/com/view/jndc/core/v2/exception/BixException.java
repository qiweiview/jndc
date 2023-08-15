package com.view.jndc.core.v2.exception;

public class BixException extends RuntimeException {
    public BixException(String message) {
        super(message);
    }

    public BixException(Throwable cause) {
        super(cause);
    }
}
