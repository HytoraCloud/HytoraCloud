package de.lystx.hytoracloud.driver.service.minecraft.world;

import de.lystx.hytoracloud.driver.service.minecraft.entity.MinecraftEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter @AllArgsConstructor
public class MinecraftWorld implements Serializable {

    private static final long serialVersionUID = -6977488246076458689L;

    /**
     * The name of the world
     */
    private final String name;

    /**
     * The uuid of this world
     */
    private final UUID uniqueId;

    /**
     * The difficulty
     */
    private final String difficulty;

    /**
     * The game rules
     */
    private final Map<String, String> gameRules;

    /**
     * All chunks
     */
    private final List<MinecraftChunk> chunks;

    /**
     * All cached entities
     */
    private final List<MinecraftEntity> entities;
}
