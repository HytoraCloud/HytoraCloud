package de.lystx.hytoracloud.driver.cloudservices.managing.database;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerInformation;

import java.util.List;
import java.util.UUID;

public interface IDatabase {

    /**
     * Builds up connection
     */
    void connect();

    /**
     * Disconnecting
     */
    void disconnect();

    /**
     * Inserts player to database
     * @param ICloudPlayer
     */
    void registerPlayer(ICloudPlayer ICloudPlayer);

    /**
     *
     * @param uuid
     * @return if player is registered
     */
    boolean isRegistered(UUID uuid);

    /**
     * Checks connection
     * @return
     */
    boolean isConnected();

    /**
     * PlayerData
     * @param uuid
     * @return CloudPlayerData
     */
    PlayerInformation getOfflinePlayer(UUID uuid);

    /**
     * Updates Data of player
     * @param uuid
     * @param data
     */
    void saveOfflinePlayer(UUID uuid, PlayerInformation data);

    /**
     * Loads entries of database
     * @return
     */
    List<PlayerInformation> loadEntries();

    /**
     * Returns the Type of this database
     *
     * @return the type
     */
    DatabaseType getType();
}
