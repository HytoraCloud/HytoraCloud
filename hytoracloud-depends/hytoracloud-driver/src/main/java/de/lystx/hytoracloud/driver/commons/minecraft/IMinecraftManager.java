package de.lystx.hytoracloud.driver.commons.minecraft;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.commons.service.IService;

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
