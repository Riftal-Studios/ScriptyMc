package dev.riftal.minecraft.scripty.rest;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Filter;
import dev.riftal.minecraft.scripty.Scripty;
import dev.riftal.minecraft.scripty.rest.handlers.PlaceBlockHandler;
import dev.riftal.minecraft.scripty.rest.handlers.SpawnEntityHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.security.SecureRandom;
import java.util.Base64;

public class RestServer {
    private final Scripty plugin;
    private HttpServer server;
    private static final int PORT = 6060;
    private final String API_KEY;

    public RestServer(Scripty plugin) {
        this.plugin = plugin;
        this.API_KEY = initializeApiKey();
    }

    private String initializeApiKey() {
        String existingKey = plugin.getConfig().getString("api-key");
        if (existingKey != null && !existingKey.isEmpty()) {
            return existingKey;
        }

        // Generate new API key
        String newApiKey = generateSecureApiKey();

        // Save to config
        plugin.getConfig().set("api-key", newApiKey);
        plugin.saveConfig();

        // Log the new API key
        plugin.getLogger().info("Generated new API key: " + newApiKey);
        plugin.getLogger().info("This key is required for all API requests. Please save it securely.");

        // Create or update api-key.txt file
        try {
            java.nio.file.Path apiKeyFile = plugin.getDataFolder().toPath().resolve("api-key.txt");
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            java.nio.file.Files.writeString(apiKeyFile,
                    "API Key: " + newApiKey + "\n" +
                            "Generated: " + java.time.LocalDateTime.now() + "\n" +
                            "Keep this key secure and use it in your requests with the X-API-Key header."
            );
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save API key to file: " + e.getMessage());
        }

        return newApiKey;
    }

    private String generateSecureApiKey() {
        // Generate a more secure API key using SecureRandom
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(randomBytes);

        // Convert to base64 and remove any non-alphanumeric characters
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
                .replaceAll("[^a-zA-Z0-9]", "")
                .substring(0, 32); // Trim to 32 characters
    }

    private class ApiKeyFilter extends Filter {
        @Override
        public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
            String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");

            if (apiKey == null || !apiKey.equals(API_KEY)) {
                String response = "Unauthorized: Invalid or missing API key. Include your API key in the X-API-Key header.";
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                String jsonResponse = "{\"error\": \"" + response + "\"}";
                exchange.sendResponseHeaders(401, jsonResponse.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes());
                }
                return;
            }

            chain.doFilter(exchange);
        }

        @Override
        public String description() {
            return "API Key Validation Filter";
        }
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Create the API key filter
            ApiKeyFilter apiKeyFilter = new ApiKeyFilter();

            // Register endpoints with filter
            registerEndpoints(apiKeyFilter);

            server.setExecutor(null);
            server.start();

            plugin.getLogger().info("REST API server started on port " + PORT);
            plugin.getLogger().info("API Key location: plugins/Scripty/api-key.txt");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to start REST API server: " + e.getMessage());
        }
    }

    private void registerEndpoints(ApiKeyFilter apiKeyFilter) {
        // Create contexts with filters
        HttpContext spawnContext = server.createContext("/api/spawn", new SpawnEntityHandler(plugin));
        HttpContext blockContext = server.createContext("/api/block", new PlaceBlockHandler(plugin));

        // Add the API key filter to each context
        List<Filter> filters = new ArrayList<>();
        filters.add(apiKeyFilter);

        spawnContext.getFilters().addAll(filters);
        blockContext.getFilters().addAll(filters);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().info("REST API server stopped");
        }
    }

    // Optional: Method to get the current API key (if needed elsewhere in your plugin)
    public String getApiKey() {
        return API_KEY;
    }
}
