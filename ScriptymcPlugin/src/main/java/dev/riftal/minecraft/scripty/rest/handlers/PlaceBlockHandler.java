package dev.riftal.minecraft.scripty.rest.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import dev.riftal.minecraft.scripty.rest.models.ApiResponse;
import dev.riftal.minecraft.scripty.rest.models.BlockRequest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class PlaceBlockHandler extends BaseHandler {
    private final Gson gson = new Gson();

    public PlaceBlockHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Only allow POST requests
        if (!exchange.getRequestMethod().equals("POST")) {
            sendResponse(exchange, new ApiResponse(405, "Method not allowed", null));
            return;
        }

        try {
            // Read and parse the request body
            String requestBody = readRequestBody(exchange);
            BlockRequest blockRequest = gson.fromJson(requestBody, BlockRequest.class);

            // Validate the request
            if (!validateRequest(blockRequest)) {
                sendResponse(exchange, new ApiResponse(400, "Invalid request parameters", null));
                return;
            }

            // Place the block on the main server thread
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        boolean success = placeBlock(blockRequest);
                        if (success) {
                            sendResponse(exchange, new ApiResponse(200, "Block placed successfully", null));
                        } else {
                            sendResponse(exchange, new ApiResponse(400, "Failed to place block", null));
                        }
                    } catch (Exception e) {
                        try {
                            sendResponse(exchange, new ApiResponse(500, "Internal server error: " + e.getMessage(), null));
                        } catch (IOException ioException) {
                            plugin.getLogger().severe("Failed to send error response: " + ioException.getMessage());
                        }
                    }
                }
            }.runTask(plugin);

        } catch (Exception e) {
            sendResponse(exchange, new ApiResponse(400, "Invalid request: " + e.getMessage(), null));
        }
    }

    private boolean validateRequest(BlockRequest request) {
        if (request == null) return false;
        if (request.getWorld() == null || request.getWorld().isEmpty()) return false;
        if (request.getMaterial() == null || request.getMaterial().isEmpty()) return false;

        try {
            Material.valueOf(request.getMaterial().toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    private boolean placeBlock(BlockRequest request) {
        try {
            World world = plugin.getServer().getWorld(request.getWorld());
            if (world == null) return false;

            Location location = new Location(world, request.getX(), request.getY(), request.getZ());
            Material material = Material.valueOf(request.getMaterial().toUpperCase());

            // Check if the location is loaded
            if (!location.getChunk().isLoaded()) {
                location.getChunk().load();
            }

            // Place the block
            return location.getBlock().setType(material);
        } catch (Exception e) {
            plugin.getLogger().severe("Error placing block: " + e.getMessage());
            return false;
        }
    }
}
