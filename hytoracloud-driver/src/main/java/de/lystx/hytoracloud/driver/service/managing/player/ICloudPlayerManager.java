package de.lystx.hytoracloud.driver.service.managing.player;

import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.commons.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.managing.player.impl.PlayerInformation;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface ICloudPlayerManager extends Iterable<CloudPlayer> {

    /**
     * Gets all {@link CloudPlayer}s that are on the whole network
     *
     * @return list of players
     */
    List<CloudPlayer> getOnlinePlayers();

    /**
     * Sets the onlinePlayers (cached list)
     *
     * @param cloudPlayers the players
     */
    void setOnlinePlayers(List<CloudPlayer> cloudPlayers);

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
     * Gets an {@link CloudPlayer} by its name
     *
     * @param name the name of the player
     * @return cached cloudPlayer
     */
    CloudPlayer getCachedPlayer(String name);

    /**
     * Gets an {@link CloudPlayer} by its uuid
     *
     * @param uniqueId the uuid of the player
     * @return cached cloudPlayer
     */
    CloudPlayer getCachedPlayer(UUID uniqueId);

    /**
     * Gets an {@link CloudPlayer} by response
     * This will be slower but 100% sync with the cloud
     *
     * @param name the name of the player
     * @return cloudPlayer from cloud
     */
    CloudPlayer getPlayer(String name);

    /**
     * Gets an {@link CloudPlayer} by response
     * This will be slower but 100% sync with the cloud
     *
     * @param uniqueId the uuid of the player
     * @return cloudPlayer from cloud
     */
    CloudPlayer getPlayer(UUID uniqueId);

    /**
     * Updates a given {@link CloudPlayer}
     *
     * @param cloudPlayer the player to update
     */
    void update(CloudPlayer cloudPlayer);

    /**
     * Registers a given {@link CloudPlayer}
     *
     * @param cloudPlayer the player
     */
    void registerPlayer(CloudPlayer cloudPlayer);

    /**
     * Unregisters a given {@link CloudPlayer}
     *
     * @param cloudPlayer the player
     */
    void unregisterPlayer(CloudPlayer cloudPlayer);

    /**
     * Gets an {@link CloudPlayer} with consumer and async
     *
     * @param name the name of the player
     * @param consumer the consumer to accept the cloudPlayer
     */
    void getAsync(String name, Consumer<CloudPlayer> consumer);

    /**
     * Gets an {@link CloudPlayer} with consumer and async
     *
     * @param uniqueId the uuid of the player
     * @param consumer the consumer to accept the cloudPlayer
     */
    void getAsync(UUID uniqueId, Consumer<CloudPlayer> consumer);

    /**
     * Lists all {@link CloudPlayer}s on a given {@link Service}
     *
     * @param service the service
     * @return list of players on this service
     */
    List<CloudPlayer> getPlayersOnServer(Service service);

    /**
     * Lists all {@link CloudPlayer}s on a given {@link ServiceGroup}
     *
     * @param serviceGroup the group
     * @return list of players on this service
     */
    List<CloudPlayer> getPlayersOnGroup(ServiceGroup serviceGroup);

}
