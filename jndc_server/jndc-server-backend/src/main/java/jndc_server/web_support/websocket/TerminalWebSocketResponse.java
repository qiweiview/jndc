package jndc_server.web_support.websocket;

import lombok.Data;

@Data
public class TerminalWebSocketResponse {

    public static final String EVENT_READY = "terminal.ready";
    public static final String EVENT_OUTPUT = "terminal.output";
    public static final String EVENT_EXIT = "terminal.exit";
    public static final String EVENT_ERROR = "terminal.error";

    private String event;

    private String sessionId;

    private String data;

    private Integer exitCode;

    private String message;

    private String shellType;

    public static TerminalWebSocketResponse ready(String sessionId, String shellType) {
        TerminalWebSocketResponse response = new TerminalWebSocketResponse();
        response.setEvent(EVENT_READY);
        response.setSessionId(sessionId);
        response.setShellType(shellType);
        return response;
    }

    public static TerminalWebSocketResponse output(String sessionId, String data) {
        TerminalWebSocketResponse response = new TerminalWebSocketResponse();
        response.setEvent(EVENT_OUTPUT);
        response.setSessionId(sessionId);
        response.setData(data);
        return response;
    }

    public static TerminalWebSocketResponse exit(String sessionId, Integer exitCode) {
        TerminalWebSocketResponse response = new TerminalWebSocketResponse();
        response.setEvent(EVENT_EXIT);
        response.setSessionId(sessionId);
        response.setExitCode(exitCode);
        return response;
    }

    public static TerminalWebSocketResponse error(String sessionId, String message) {
        TerminalWebSocketResponse response = new TerminalWebSocketResponse();
        response.setEvent(EVENT_ERROR);
        response.setSessionId(sessionId);
        response.setMessage(message);
        return response;
    }
}
