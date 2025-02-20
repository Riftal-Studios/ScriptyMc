package dev.riftal.minecraft.scripty;

import dev.riftal.minecraft.scripty.rest.RestServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class Scripty extends JavaPlugin {

    private RestServer restServer;

    @Override
    public void onEnable() {
        restServer = new RestServer(this);
        restServer.start();
    }

    @Override
    public void onDisable() {
        if (restServer != null) {
            restServer.stop();
        }
    }
}
