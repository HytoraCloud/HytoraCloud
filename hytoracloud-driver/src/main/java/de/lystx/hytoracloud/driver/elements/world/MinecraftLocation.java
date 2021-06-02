package de.lystx.hytoracloud.driver.elements.world;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class MinecraftLocation {

    /**
     * The XYZ Coordinates of this location
     */
    private final double x, y, z;

    /**
     * The yaw and pitch (player rotation)
     */
    private final float yaw, pitch;

    /**
     * The world of this location
     */
    private final String world;

}
