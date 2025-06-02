package dev.riftal.minecraft.scripty.rest.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import dev.riftal.minecraft.scripty.rest.models.ApiResponse;
import dev.riftal.minecraft.scripty.rest.models.BlockRequest;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

            // Use CompletableFuture to handle async response
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            
            // Place the block on the main server thread
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        boolean success = placeBlock(blockRequest);
                        future.complete(success);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }
            }.runTask(plugin);

            // Wait for the result and send response
            try {
                boolean success = future.get();
                if (success) {
                    sendResponse(exchange, new ApiResponse(200, "Block placed successfully", null));
                } else {
                    sendResponse(exchange, new ApiResponse(400, "Failed to place block", null));
                }
            } catch (Exception e) {
                sendResponse(exchange, new ApiResponse(500, "Internal server error: " + e.getMessage(), null));
            }

        } catch (Exception e) {
            sendResponse(exchange, new ApiResponse(400, "Invalid request: " + e.getMessage(), null));
        }
    }

    private boolean validateRequest(BlockRequest request) {
        if (request == null) {
            return false;
        }
        if (request.getWorld() == null || request.getWorld().isEmpty()) {
            return false;
        }
        if (request.getMaterial() == null || request.getMaterial().isEmpty()) {
            return false;
        }

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
            if (world == null) {
                return false;
            }

            Location location = new Location(world, request.getX(), request.getY(), request.getZ());
            Material material = Material.valueOf(request.getMaterial().toUpperCase());

            // Check if the location is loaded
            if (!location.getChunk().isLoaded()) {
                location.getChunk().load();
            }

            // Place the block
            location.getBlock().setType(material);
            return true; // Return true if no exception was thrown
        } catch (Exception e) {
            plugin.getLogger().severe("Error placing block: " + e.getMessage());
            return false;
        }
    }
}