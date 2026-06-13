package jndc.core.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class TerminalControlMessage implements Serializable {

    private static final long serialVersionUID = 2191320125597025705L;

    public static final String ACTION_OPEN = "OPEN";
    public static final String ACTION_INPUT = "INPUT";
    public static final String ACTION_OUTPUT = "OUTPUT";
    public static final String ACTION_CLOSE = "CLOSE";
    public static final String ACTION_EXIT = "EXIT";
    public static final String ACTION_ERROR = "ERROR";
    public static final String ACTION_RESIZE = "RESIZE";

    private String action;

    private String sessionId;

    private String clientId;

    private String data;

    private Integer cols;

    private Integer rows;

    private Integer exitCode;

    private String message;

    private String shellType;
}
