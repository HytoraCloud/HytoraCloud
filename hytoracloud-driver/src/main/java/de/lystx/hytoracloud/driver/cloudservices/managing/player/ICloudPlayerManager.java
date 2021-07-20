package de.lystx.hytoracloud.driver.cloudservices.managing.player;

import de.lystx.hytoracloud.driver.cloudservices.other.ObjectPool;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;

import java.util.List;
import java.util.UUID;

public interface ICloudPlayerManager extends ObjectPool<ICloudPlayer> {

    /**
     * Gets a list of all {@link OfflinePlayer}s from cache
     *
     * @return list of offlineplayers
     */
    List<OfflinePlayer> getOfflinePlayers();

    /**
     * Gets an {@link OfflinePlayer} by its name
     *
     * @param name the name of the player
     * @return cached offlinePlayer
     */
    OfflinePlayer getOfflinePlayer(String name);

    /**
     * Gets an {@link OfflinePlayer} by its uuid
     *
     * @param uniqueId the uuid of the player
     * @return cached offlinePlayer
     */
    OfflinePlayer getOfflinePlayer(UUID uniqueId);

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
