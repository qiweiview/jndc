package jndc_client.core;

import jndc.utils.YmlParser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JNDCClientConfigParsingTest {

    private final YmlParser ymlParser = new YmlParser();

    @Test
    public void shouldParseClientConfigWithoutManageConfig() {
        String yaml = """
                secrete: "secret"
                loglevel: "info"
                serverIp: "127.0.0.1"
                serverPort: 1081
                autoReleaseTimeOut: 600000
                authMode: 1
                clientServiceDescriptions: []
                """;

        JNDCClientConfig config = ymlParser.parseFile(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                JNDCClientConfig.class
        );

        assertNotNull(config);
        assertEquals("secret", config.getSecrete());
        assertEquals(1081, config.getServerPort());
        assertEquals(1, config.getAuthMode());
    }

    @Test
    public void shouldIgnoreLegacyManageConfig() {
        String yaml = """
                secrete: "secret"
                loglevel: "info"
                serverIp: "127.0.0.1"
                serverPort: 1081
                autoReleaseTimeOut: 600000
                authMode: 1
                manageConfig:
                  managementApiPort: 1778
                  useSsl: false
                  loginName: "legacy"
                  loginPassWord: "legacy"
                clientServiceDescriptions: []
                """;

        JNDCClientConfig config = ymlParser.parseFile(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                JNDCClientConfig.class
        );

        assertNotNull(config);
        assertEquals("127.0.0.1", config.getServerIp());
        assertEquals(600000L, config.getAutoReleaseTimeOut());
    }
}
