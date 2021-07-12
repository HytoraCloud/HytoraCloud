package de.lystx.hytoracloud.driver.cloudservices.managing.player;

import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerInformation;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface ICloudPlayerManager extends Iterable<ICloudPlayer> {

    /**
     * Gets all {@link ICloudPlayer}s that are on the whole network
     *
     * @return list of players
     */
    List<ICloudPlayer> getOnlinePlayers();

    /**
     * Sets the onlinePlayers (cached list)
     *
     * @param ICloudPlayers the players
     */
    void setOnlinePlayers(List<ICloudPlayer> ICloudPlayers);

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
     * Gets an {@link ICloudPlayer} by its name
     *
     * @param name the name of the player
     * @return cached cloudPlayer
     */
    ICloudPlayer getCachedPlayer(String name);

    /**
     * Gets an {@link ICloudPlayer} by its uuid
     *
     * @param uniqueId the uuid of the player
     * @return cached cloudPlayer
     */
    ICloudPlayer getCachedPlayer(UUID uniqueId);

    /**
     * Gets an {@link ICloudPlayer} by response
     * This will be slower but 100% sync with the cloud
     *
     * @param name the name of the player
     * @return cloudPlayer from cloud
     */
    ICloudPlayer getPlayer(String name);

    /**
     * Gets an {@link ICloudPlayer} by response
     * This will be slower but 100% sync with the cloud
     *
     * @param uniqueId the uuid of the player
     * @return cloudPlayer from cloud
     */
    ICloudPlayer getPlayer(UUID uniqueId);

    /**
     * Updates a given {@link ICloudPlayer}
     *
     * @param ICloudPlayer the player to update
     */
    void update(ICloudPlayer ICloudPlayer);

    /**
     * Registers a given {@link ICloudPlayer}
     *
     * @param ICloudPlayer the player
     */
    void registerPlayer(ICloudPlayer ICloudPlayer);

    /**
     * Unregisters a given {@link ICloudPlayer}
     *
     * @param ICloudPlayer the player
     */
    void unregisterPlayer(ICloudPlayer ICloudPlayer);

    /**
     * Gets an {@link ICloudPlayer} with consumer and async
     *
     * @param name the name of the player
     * @param consumer the consumer to accept the cloudPlayer
     */
    void getAsync(String name, Consumer<ICloudPlayer> consumer);

    /**
     * Gets an {@link ICloudPlayer} with consumer and async
     *
     * @param uniqueId the uuid of the player
     * @param consumer the consumer to accept the cloudPlayer
     */
    void getAsync(UUID uniqueId, Consumer<ICloudPlayer> consumer);

    /**
     * Lists all {@link ICloudPlayer}s on a given {@link IService}
     *
     * @param IService the service
     * @return list of players on this service
     */
    List<ICloudPlayer> getPlayersOnServer(IService IService);

    /**
     * Lists all {@link ICloudPlayer}s on a given {@link IServiceGroup}
     *
     * @param IServiceGroup the group
     * @return list of players on this service
     */
    List<ICloudPlayer> getPlayersOnGroup(IServiceGroup IServiceGroup);

}
