package de.lystx.cloudsystem.library.service.database;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.player.impl.DefaultCloudPlayerData;

import java.util.List;
import java.util.UUID;

public interface CloudDatabase {

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
     * @param cloudPlayer
     */
    void registerPlayer(CloudPlayer cloudPlayer);

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
    CloudPlayerData getPlayerData(UUID uuid);

    /**
     * Updates Data of player
     * @param uuid
     * @param data
     */
    void setPlayerData(UUID uuid, CloudPlayerData data);

    /**
     * Loads entries of database
     * @return
     */
    List<CloudPlayerData> loadEntries();
}
