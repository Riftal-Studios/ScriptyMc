package dev.riftal.minecraft.scripty.rest.handlers;

import com.sun.net.httpserver.HttpExchange;
import dev.riftal.minecraft.scripty.rest.models.ApiResponse;
import dev.riftal.minecraft.scripty.rest.models.SpawnRequest;
import dev.riftal.minecraft.scripty.utils.JsonUtils;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnEntityHandler extends BaseHandler {

    public SpawnEntityHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, new ApiResponse(405, "Method not allowed"));
            return;
        }

        try {
            String requestBody = readRequestBody(exchange);
            SpawnRequest request = JsonUtils.fromJson(requestBody, SpawnRequest.class);

            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    World world = Bukkit.getWorld(request.getWorld());
                    if (world == null) {
                        throw new IllegalArgumentException("World not found");
                    }

                    Location loc = new Location(world, request.getX(), request.getY(),
                            request.getZ());
                    world.spawnEntity(loc, request.getEntityType());
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to spawn entity: " + e.getMessage());
                }
            });

            sendResponse(exchange, new ApiResponse(200, "Entity spawn scheduled"));
        } catch (Exception e) {
            sendResponse(exchange, new ApiResponse(400, "Error: " + e.getMessage()));
        }
    }
}
