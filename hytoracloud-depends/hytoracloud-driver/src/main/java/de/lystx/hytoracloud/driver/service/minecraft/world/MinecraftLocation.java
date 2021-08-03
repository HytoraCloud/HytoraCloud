package de.lystx.hytoracloud.driver.service.minecraft.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter @AllArgsConstructor @ToString
public class MinecraftLocation implements Serializable {

    private static final long serialVersionUID = 6469977870942812925L;

    /**
     * The world
     */
    private final String world;

    /**
     * The XYZ coords
     */
    private final double x, y, z;

    /**
     * The yaw
     */
    private final float yaw;

    /**
     * The pitch
     */
    private final float pitch;


    /**
     * Adds coordinates to this location
     *
     * @param x the x value
     * @param y the y value
     * @param z the z value
     * @return new location
     */
    public MinecraftLocation add(int x, int y, int z) {
        return new MinecraftLocation(this.world, this.x + x, this.y + y, this.z + z, this.yaw, this.pitch);
    }
}
