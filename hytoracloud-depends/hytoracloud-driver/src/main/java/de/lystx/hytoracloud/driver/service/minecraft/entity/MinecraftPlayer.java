package de.lystx.hytoracloud.driver.service.minecraft.entity;

import de.lystx.hytoracloud.driver.service.minecraft.world.MinecraftLocation;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MinecraftPlayer extends MinecraftEntity {

    private static final long serialVersionUID = -491221979047969584L;
    /**
     * The name of the player
     */
    private final String name;

    /**
     * The health level
     */
    private final double health;

    /**
     * The food level
     */
    private final int foodLevel;

    /**
     * The xp level
     */
    private final float xpLevel;

    public MinecraftPlayer(UUID uniqueId, int entityId, String type, String customName, MinecraftLocation location, String name, double health, int foodLevel, float xpLevel) {
        super(uniqueId, entityId, type, customName, location);
        this.name = name;
        this.health = health;
        this.foodLevel = foodLevel;
        this.xpLevel = xpLevel;
    }
}
