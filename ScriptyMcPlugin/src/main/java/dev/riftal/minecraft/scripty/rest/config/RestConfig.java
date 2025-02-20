package dev.riftal.minecraft.scripty.rest.config;

public class RestConfig {

    private static final int DEFAULT_PORT = 8080;
    private int port;
    private String apiKey; // For authentication
    private boolean enableSsl;

    public RestConfig() {
        this.port = DEFAULT_PORT;
        this.enableSsl = false;
    }

    // Getters and setters
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
    }
}
