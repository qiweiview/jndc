package jndc_client.core;

import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.message.TerminalControlMessage;
import jndc.utils.InetUtils;
import jndc.utils.OSUtils;
import jndc.utils.ObjectSerializableUtils;
import jndc.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

@Slf4j
public class ClientTerminalSessionManager {

    private final TerminalProcessFactory terminalProcessFactory;

    private final Charset shellCharset = Charset.defaultCharset();

    private volatile TerminalSession activeSession;

    public ClientTerminalSessionManager() {
        this(new DefaultTerminalProcessFactory());
    }

    ClientTerminalSessionManager(TerminalProcessFactory terminalProcessFactory) {
        this.terminalProcessFactory = terminalProcessFactory;
    }

    public synchronized void handle(TerminalControlMessage message) {
        if (message == null || message.getAction() == null) {
            return;
        }

        switch (message.getAction()) {
            case TerminalControlMessage.ACTION_OPEN:
                open(message);
                break;
            case TerminalControlMessage.ACTION_INPUT:
                input(message);
                break;
            case TerminalControlMessage.ACTION_CLOSE:
                close(message.getSessionId(), false);
                break;
            case TerminalControlMessage.ACTION_RESIZE:
                break;
            default:
                log.warn("ignore unsupported terminal action {}", message.getAction());
                break;
        }
    }

    public synchronized void closeActiveSessionForDisconnect() {
        if (activeSession == null) {
            return;
        }
        close(activeSession.sessionId, true);
    }

    private synchronized void open(TerminalControlMessage message) {
        if (activeSession != null) {
            sendError(message.getSessionId(), message.getClientId(), "terminal session already active");
            return;
        }

        String shellType = OSUtils.isLinux() ? "/bin/sh" : "cmd.exe";
        try {
            Process process = terminalProcessFactory.start(shellType, new File(PathUtils.getClientWorkspace()));
            TerminalSession session = new TerminalSession(message.getSessionId(), message.getClientId(), shellType, process);
            activeSession = session;
            session.start();
            sendOpenAck(session);
        } catch (Exception e) {
            log.error("open terminal session failed", e);
            sendError(message.getSessionId(), message.getClientId(), "open terminal failed: " + e.getMessage());
        }
    }

    private synchronized void input(TerminalControlMessage message) {
        TerminalSession session = requireActiveSession(message.getSessionId(), message.getClientId());
        if (session == null) {
            return;
        }
        try {
            session.writeInput(message.getData() == null ? "" : message.getData());
        } catch (IOException e) {
            log.error("write terminal input failed", e);
            sendError(session.sessionId, session.clientId, "write terminal input failed: " + e.getMessage());
            close(session.sessionId, true);
        }
    }

    private synchronized void close(String sessionId, boolean silent) {
        TerminalSession session = activeSession;
        if (session == null || !session.sessionId.equals(sessionId)) {
            return;
        }

        activeSession = null;
        session.close(silent);
    }

    private synchronized void onProcessExit(TerminalSession session, int exitCode) {
        if (activeSession == session) {
            activeSession = null;
        }
        if (!session.suppressExitNotification) {
            sendExit(session.sessionId, session.clientId, exitCode);
        }
    }

    private synchronized void onProcessOutput(TerminalSession session, String text) {
        if (activeSession != session) {
            return;
        }
        sendOutput(session.sessionId, session.clientId, text);
    }

    private synchronized void onProcessError(TerminalSession session, Exception exception) {
        if (activeSession == session) {
            activeSession = null;
        }
        sendError(session.sessionId, session.clientId, exception.getMessage());
    }

    private TerminalSession requireActiveSession(String sessionId, String clientId) {
        TerminalSession session = activeSession;
        if (session == null || !session.sessionId.equals(sessionId)) {
            sendError(sessionId, clientId, "terminal session not found");
            return null;
        }
        return session;
    }

    private void sendOpenAck(TerminalSession session) {
        TerminalControlMessage message = baseMessage(TerminalControlMessage.ACTION_OPEN, session.sessionId, session.clientId);
        message.setShellType(session.shellType);
        sendMessage(message);
    }

    private void sendOutput(String sessionId, String clientId, String text) {
        if (text == null || "".equals(text)) {
            return;
        }
        TerminalControlMessage message = baseMessage(TerminalControlMessage.ACTION_OUTPUT, sessionId, clientId);
        message.setData(text);
        sendMessage(message);
    }

    private void sendExit(String sessionId, String clientId, int exitCode) {
        TerminalControlMessage message = baseMessage(TerminalControlMessage.ACTION_EXIT, sessionId, clientId);
        message.setExitCode(exitCode);
        sendMessage(message);
    }

    private void sendError(String sessionId, String clientId, String errorMessage) {
        TerminalControlMessage message = baseMessage(TerminalControlMessage.ACTION_ERROR, sessionId, clientId);
        message.setMessage(errorMessage);
        sendMessage(message);
    }

    private TerminalControlMessage baseMessage(String action, String sessionId, String clientId) {
        TerminalControlMessage message = new TerminalControlMessage();
        message.setAction(action);
        message.setSessionId(sessionId);
        message.setClientId(clientId);
        return message;
    }

    private void sendMessage(TerminalControlMessage terminalControlMessage) {
        NDCMessageProtocol protocol = NDCMessageProtocol.of(
                InetUtils.localInetAddress,
                InetUtils.localInetAddress,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.UN_USED_PORT,
                NDCMessageProtocol.TERMINAL_CONTROL
        );
        protocol.setData(ObjectSerializableUtils.object2bytes(terminalControlMessage));
        UniqueBeanManage.getBean(JNDCClientConfigCenter.class).addMessageToSendQueue(protocol);
    }

    interface TerminalProcessFactory {
        Process start(String shellType, File workingDirectory) throws IOException;
    }

    private class TerminalSession {
        private final String sessionId;
        private final String clientId;
        private final String shellType;
        private final Process process;
        private volatile boolean closed;
        private volatile boolean suppressExitNotification;

        private TerminalSession(String sessionId, String clientId, String shellType, Process process) {
            this.sessionId = sessionId;
            this.clientId = clientId;
            this.shellType = shellType;
            this.process = process;
        }

        private void start() {
            Thread readThread = new Thread(() -> readOutput(process.getInputStream()));
            readThread.setDaemon(true);
            readThread.setName("jndc-terminal-read");
            readThread.start();

            Thread exitThread = new Thread(() -> {
                try {
                    int exitCode = process.waitFor();
                    onProcessExit(this, exitCode);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (!closed) {
                        onProcessError(this, e);
                    }
                }
            });
            exitThread.setDaemon(true);
            exitThread.setName("jndc-terminal-exit");
            exitThread.start();
        }

        private void readOutput(InputStream inputStream) {
            byte[] buffer = new byte[1024];
            try {
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    String text = new String(buffer, 0, len, shellCharset);
                    onProcessOutput(this, text);
                }
            } catch (IOException e) {
                if (!closed) {
                    onProcessError(this, e);
                }
            }
        }

        private void writeInput(String data) throws IOException {
            OutputStream outputStream = process.getOutputStream();
            outputStream.write(data.getBytes(shellCharset));
            outputStream.flush();
        }

        private void close(boolean silent) {
            suppressExitNotification = silent;
            closed = true;
            process.destroy();
            try {
                process.getOutputStream().close();
            } catch (IOException e) {
                log.debug("close terminal stdin failed: {}", e.getMessage());
            }
            try {
                process.getInputStream().close();
            } catch (IOException e) {
                log.debug("close terminal stdout failed: {}", e.getMessage());
            }
        }
    }

    private static class DefaultTerminalProcessFactory implements TerminalProcessFactory {

        @Override
        public Process start(String shellType, File workingDirectory) throws IOException {
            ProcessBuilder processBuilder = new ProcessBuilder(shellType);
            processBuilder.directory(workingDirectory);
            processBuilder.redirectErrorStream(true);
            return processBuilder.start();
        }
    }
}
