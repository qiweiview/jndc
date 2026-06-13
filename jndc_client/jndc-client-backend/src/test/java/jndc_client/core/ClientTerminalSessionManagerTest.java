package jndc_client.core;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.message.TerminalControlMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClientTerminalSessionManagerTest {

    @Before
    public void setUp() throws Exception {
        clearBeanMap();
    }

    @After
    public void tearDown() throws Exception {
        clearBeanMap();
    }

    @Test
    public void shouldRunOpenInputCloseLifecycle() throws Exception {
        EmbeddedChannel tunnel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        JNDCClientConfigCenter configCenter = new JNDCClientConfigCenter();
        configCenter.registerMessageChannel(tunnel.pipeline().firstContext());
        UniqueBeanManage.registerBean(configCenter);

        FakeProcess process = new FakeProcess();
        ClientTerminalSessionManager manager = new ClientTerminalSessionManager((shellType, workingDirectory) -> process);

        manager.handle(message(TerminalControlMessage.ACTION_OPEN, "session-a", "client-a", null));
        TerminalControlMessage openAck = readTerminalMessage(tunnel);
        assertEquals(TerminalControlMessage.ACTION_OPEN, openAck.getAction());

        manager.handle(message(TerminalControlMessage.ACTION_INPUT, "session-a", "client-a", "pwd\r"));
        assertEquals("pwd\r", process.stdinContent());

        process.pushOutput("hello\n");
        TerminalControlMessage output = waitForMessage(tunnel, TerminalControlMessage.ACTION_OUTPUT);
        assertEquals("hello\n", output.getData());

        manager.handle(message(TerminalControlMessage.ACTION_CLOSE, "session-a", "client-a", null));
        TerminalControlMessage exit = waitForMessage(tunnel, TerminalControlMessage.ACTION_EXIT);
        assertEquals(Integer.valueOf(0), exit.getExitCode());
    }

    @Test
    public void shouldSendExitWhenShellStops() throws Exception {
        EmbeddedChannel tunnel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        JNDCClientConfigCenter configCenter = new JNDCClientConfigCenter();
        configCenter.registerMessageChannel(tunnel.pipeline().firstContext());
        UniqueBeanManage.registerBean(configCenter);

        FakeProcess process = new FakeProcess();
        ClientTerminalSessionManager manager = new ClientTerminalSessionManager((shellType, workingDirectory) -> process);

        manager.handle(message(TerminalControlMessage.ACTION_OPEN, "session-a", "client-a", null));
        readTerminalMessage(tunnel);

        process.complete(7);

        TerminalControlMessage exit = waitForMessage(tunnel, TerminalControlMessage.ACTION_EXIT);
        assertEquals(Integer.valueOf(7), exit.getExitCode());
    }

    @Test
    public void shouldRejectDuplicateOpen() throws Exception {
        EmbeddedChannel tunnel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        JNDCClientConfigCenter configCenter = new JNDCClientConfigCenter();
        configCenter.registerMessageChannel(tunnel.pipeline().firstContext());
        UniqueBeanManage.registerBean(configCenter);

        FakeProcess process = new FakeProcess();
        ClientTerminalSessionManager manager = new ClientTerminalSessionManager((shellType, workingDirectory) -> process);

        manager.handle(message(TerminalControlMessage.ACTION_OPEN, "session-a", "client-a", null));
        readTerminalMessage(tunnel);

        manager.handle(message(TerminalControlMessage.ACTION_OPEN, "session-b", "client-a", null));

        TerminalControlMessage error = waitForMessage(tunnel, TerminalControlMessage.ACTION_ERROR);
        assertEquals("terminal session already active", error.getMessage());
    }

    private TerminalControlMessage message(String action, String sessionId, String clientId, String data) {
        TerminalControlMessage message = new TerminalControlMessage();
        message.setAction(action);
        message.setSessionId(sessionId);
        message.setClientId(clientId);
        message.setData(data);
        return message;
    }

    private TerminalControlMessage readTerminalMessage(EmbeddedChannel tunnel) {
        NDCMessageProtocol protocol = tunnel.readOutbound();
        assertNotNull(protocol);
        return protocol.getObject(TerminalControlMessage.class);
    }

    private TerminalControlMessage waitForMessage(EmbeddedChannel tunnel, String action) throws Exception {
        long deadline = System.currentTimeMillis() + 3000L;
        while (System.currentTimeMillis() < deadline) {
            NDCMessageProtocol protocol = tunnel.readOutbound();
            if (protocol == null) {
                Thread.sleep(20L);
                continue;
            }
            TerminalControlMessage message = protocol.getObject(TerminalControlMessage.class);
            if (action.equals(message.getAction())) {
                return message;
            }
        }
        throw new AssertionError("missing terminal message " + action);
    }

    private void clearBeanMap() throws Exception {
        Field field = UniqueBeanManage.class.getDeclaredField("map");
        field.setAccessible(true);
        Map map = (Map) field.get(null);
        map.clear();
    }

    private static class FakeProcess extends Process {

        private final ByteArrayOutputStream stdin = new ByteArrayOutputStream();
        private final PipeStream pipeStream = new PipeStream();
        private final CountDownLatch exitLatch = new CountDownLatch(1);
        private final AtomicReference<Integer> exitCode = new AtomicReference<Integer>();

        @Override
        public OutputStream getOutputStream() {
            return stdin;
        }

        @Override
        public InputStream getInputStream() {
            return pipeStream.getInputStream();
        }

        @Override
        public InputStream getErrorStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public int waitFor() throws InterruptedException {
            exitLatch.await(3, TimeUnit.SECONDS);
            return exitCode.get() == null ? 0 : exitCode.get();
        }

        @Override
        public int exitValue() {
            if (exitCode.get() == null) {
                throw new IllegalThreadStateException();
            }
            return exitCode.get();
        }

        @Override
        public void destroy() {
            complete(0);
        }

        public void pushOutput(String text) throws IOException {
            pipeStream.write(text.getBytes(Charset.defaultCharset()));
        }

        public void complete(int code) {
            if (exitCode.compareAndSet(null, code)) {
                pipeStream.close();
                exitLatch.countDown();
            }
        }

        public String stdinContent() {
            return new String(stdin.toByteArray(), Charset.defaultCharset());
        }
    }

    private static class PipeStream {
        private final java.io.PipedInputStream inputStream;
        private final java.io.PipedOutputStream outputStream;

        private PipeStream() {
            try {
                inputStream = new java.io.PipedInputStream();
                outputStream = new java.io.PipedOutputStream(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private InputStream getInputStream() {
            return inputStream;
        }

        private void write(byte[] bytes) throws IOException {
            outputStream.write(bytes);
            outputStream.flush();
        }

        private void close() {
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
