package de.lystx.hytoracloud.driver.cloudservices.managing.player;

import de.lystx.hytoracloud.driver.cloudservices.other.ObjectPool;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerInformation;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface ICloudPlayerManager extends ObjectPool<ICloudPlayer> {

    /**
     * Gets a list of all {@link PlayerInformation}s from cache
     *
     * @return list of offlineplayers
     */
    List<PlayerInformation> getOfflinePlayers();

    /**
     * Gets an {@link PlayerInformation} by its name
     *
     * @param name the name of the player
     * @return cached offlinePlayer
     */
    PlayerInformation getOfflinePlayer(String name);

    /**
     * Gets an {@link PlayerInformation} by its uuid
     *
     * @param uniqueId the uuid of the player
     * @return cached offlinePlayer
     */
    PlayerInformation getOfflinePlayer(UUID uniqueId);

    /**
     * Updates a given {@link ICloudPlayer}
     *
     * @param cloudPlayer the player to update
     */
    void update(ICloudPlayer cloudPlayer);

    /**
     * Registers a given {@link ICloudPlayer}
     *
     * @param cloudPlayer the player
     */
    void registerPlayer(ICloudPlayer cloudPlayer);

    /**
     * Unregisters a given {@link ICloudPlayer}
     *
     * @param cloudPlayer the player
     */
    void unregisterPlayer(ICloudPlayer cloudPlayer);

}
