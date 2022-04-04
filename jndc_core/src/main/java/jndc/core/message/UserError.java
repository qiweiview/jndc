package jndc.core.message;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserError implements Serializable {
    public static final int SERVER_ERROR = 500;

    private static final long serialVersionUID = 2996922883540744896L;

    private int code;

    private String description;


}
