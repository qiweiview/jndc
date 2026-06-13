package jndc_server.web_support.websocket;

import lombok.Data;

@Data
public class TerminalWebSocketRequest {

    public static final String EVENT_OPEN = "terminal.open";
    public static final String EVENT_INPUT = "terminal.input";
    public static final String EVENT_RESIZE = "terminal.resize";
    public static final String EVENT_CLOSE = "terminal.close";

    private String event;

    private String sessionId;

    private String clientId;

    private String data;

    private Integer cols;

    private Integer rows;
}
