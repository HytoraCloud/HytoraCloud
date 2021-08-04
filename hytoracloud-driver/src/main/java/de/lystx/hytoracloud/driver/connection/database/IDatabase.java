package de.lystx.hytoracloud.driver.connection.database;

import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.player.required.OfflinePlayer;
import de.lystx.hytoracloud.driver.connection.database.impl.DatabaseType;

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
     *
     * @param cloudPlayer the player
     */
    void createEntry(ICloudPlayer cloudPlayer);

    /**
     * Checks if a player is already in database
     *
     * @param uuid the uuid of the player
     * @return if player is registered
     */
    boolean isRegistered(UUID uuid);

    /**
     * Checks connection
     *
     * @return boolean
     */
    boolean isConnected();

    /**
     * Gets the data of a player
     *
     * @param uuid the uuid
     * @return CloudPlayerData
     */
    OfflinePlayer getEntry(UUID uuid);

    /**
     * Updates Data of player
     *
     * @param uuid the uuid
     * @param data the data
     */
    void saveEntry(UUID uuid, OfflinePlayer data);

    /**
     * Loads entries of database
     *
     * @return list of entries
     */
    List<OfflinePlayer> loadEntries();

    /**
     * Returns the Type of this database
     *
     * @return the type
     */
    DatabaseType getType();
}
