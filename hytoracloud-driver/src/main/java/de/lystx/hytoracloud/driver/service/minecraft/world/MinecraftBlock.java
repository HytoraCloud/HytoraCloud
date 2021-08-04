package de.lystx.hytoracloud.driver.service.minecraft.world;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class MinecraftBlock implements Serializable {
    private static final long serialVersionUID = -2164263865478584260L;


    /**
     * Extra data like subId
     */
    private final byte data;

    /**
     * The material type
     */
    private final String type;

    /**
     * The location
     */
    private final MinecraftLocation location;

}
