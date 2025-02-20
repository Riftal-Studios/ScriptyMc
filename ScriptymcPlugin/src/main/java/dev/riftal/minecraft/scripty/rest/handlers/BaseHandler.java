package dev.riftal.minecraft.scripty.rest.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.riftal.minecraft.scripty.rest.models.ApiResponse;
import dev.riftal.minecraft.scripty.utils.JsonUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseHandler implements HttpHandler {
    protected final JavaPlugin plugin;

    public BaseHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    protected void sendResponse(HttpExchange exchange, ApiResponse response) throws IOException {
        String jsonResponse = JsonUtils.toJson(response);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(response.getStatus(), jsonResponse.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes());
    }
}
