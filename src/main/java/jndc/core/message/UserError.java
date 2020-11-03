package jndc.core.message;

import java.io.Serializable;

public class UserError  implements Serializable {


    public static final int  AUTH_ERROR=403;
    public static final int  SERVER_ERROR=500;
    private static final long serialVersionUID = 2996922883540744896L;


    private int code;
    private String description;

    public boolean isAuthFail(){
        return getCode()==403;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
