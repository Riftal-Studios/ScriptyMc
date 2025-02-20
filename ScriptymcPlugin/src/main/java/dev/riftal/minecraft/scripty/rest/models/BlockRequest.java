package dev.riftal.minecraft.scripty.rest.models;

public class BlockRequest {
    private String world;
    private double x;
    private double y;
    private double z;
    private String material;


    public BlockRequest(String world, double x, double y, double z, String material) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
}
