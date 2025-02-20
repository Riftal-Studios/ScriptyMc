package dev.riftal.minecraft.scripty.rest;

import com.sun.net.httpserver.HttpServer;
import dev.riftal.minecraft.scripty.Scripty;
import dev.riftal.minecraft.scripty.rest.handlers.PlaceBlockHandler;
import dev.riftal.minecraft.scripty.rest.handlers.SpawnEntityHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestServer {
    private final Scripty plugin;
    private HttpServer server;
    private static final int PORT = 6060;

    public RestServer(Scripty plugin) {
        this.plugin = plugin;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            registerEndpoints();
            server.setExecutor(null);
            server.start();
            plugin.getLogger().info("REST API server started on port " + PORT);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to start REST API server: " + e.getMessage());
        }
    }

    private void registerEndpoints() {
        server.createContext("/api/spawn", new SpawnEntityHandler(plugin));
        server.createContext("/api/block", new PlaceBlockHandler(plugin));
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().info("REST API server stopped");
        }
    }
}
