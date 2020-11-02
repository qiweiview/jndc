package jndc.core.config;

public class UnifiedConfiguration {
    private ServerConfig serverConfig;

    private ClientConfig clientConfig;


    @Override
    public String toString() {
        return "UnifiedConfiguration{" +
                "serverConfig=" + serverConfig +
                ", clientConfig=" + clientConfig +
                '}';
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
