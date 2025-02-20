package dev.riftal.minecraft.scripty.rest.models;

import org.bukkit.entity.EntityType;

public class SpawnRequest {
    private EntityType entityType;
    private double x;
    private double y;
    private double z;
    private String world;

    // Getters and setters
    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }
    public String getWorld() { return world; }
    public void setWorld(String world) { this.world = world; }
}
