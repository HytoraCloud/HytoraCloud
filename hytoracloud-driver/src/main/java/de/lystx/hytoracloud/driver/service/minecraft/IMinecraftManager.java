package de.lystx.hytoracloud.driver.service.minecraft;

import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.service.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.service.IService;

public interface IMinecraftManager {

    /**
     * Gets the location of a {@link ICloudPlayer} on a given {@link IService}
     *
     * @param service the service
     * @param player the player
     * @return location of player
     */
    MinecraftLocation getLocation(IService service, ICloudPlayer player);

    /**
     * Loads a whole {@link MinecraftInfo} of
     * a given {@link IService}
     *
     * @param service the service
     * @return info
     */
    MinecraftInfo getInfo(IService service);
}
