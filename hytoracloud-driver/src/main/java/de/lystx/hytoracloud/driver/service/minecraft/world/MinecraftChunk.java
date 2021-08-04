package de.lystx.hytoracloud.driver.service.minecraft.world;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter @AllArgsConstructor
public class MinecraftChunk implements Serializable {

    private static final long serialVersionUID = -5065789371621666296L;

    /**
     * The XZ-Coords
     */
    private final int x, z;

    /**
     * If the chunk is loaded
     */
    private final boolean loaded;

    /**
     * All cached blocks
     */
    private final List<MinecraftBlock> blocks;

    /**
     * Gets a block by XYZ-Coords
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return block or null
     */
    public MinecraftBlock getBlock(int x, int y, int z) {
        return this.blocks.stream().filter(block -> block.getLocation().getX() == x && block.getLocation().getY() == y && block.getLocation().getZ() == z).findFirst().orElse(null);
    }
}
