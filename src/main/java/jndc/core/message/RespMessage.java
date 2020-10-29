package jndc.core.message;

import java.io.Serializable;

public class RespMessage implements Serializable {


    private static final long serialVersionUID = -1442343536552369754L;

    private String message;

    public RespMessage() {
    }

    public RespMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RespMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
