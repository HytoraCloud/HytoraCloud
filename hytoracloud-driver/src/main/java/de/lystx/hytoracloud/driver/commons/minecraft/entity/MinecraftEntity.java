package de.lystx.hytoracloud.driver.commons.minecraft.entity;

import de.lystx.hytoracloud.driver.commons.minecraft.world.MinecraftLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter @AllArgsConstructor
public class MinecraftEntity implements Serializable {

    private static final long serialVersionUID = 6883852985373417640L;
    /**
     * The uuid of it
     */
    private final UUID uniqueId;

    /**
     * The entity id
     */
    private final int entityId;

    /**
     * The entity type
     */
    private final String type;

    /**
     * The custom name if set
     */
    private final String customName;

    /**
     * the location
     */
    private final MinecraftLocation location;

}
