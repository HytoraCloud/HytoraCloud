package de.lystx.cloudsystem.library.service.database;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.player.impl.DefaultCloudPlayerData;

import java.util.List;
import java.util.UUID;

public interface CloudDatabase {

    void connect();

    void disconnect();

    void registerPlayer(CloudPlayer cloudPlayer);

    boolean isRegistered(UUID uuid);

    boolean isConnected();

    CloudPlayerData getPlayerData(UUID uuid);

    void setPlayerData(UUID uuid, CloudPlayerData data);

    List<CloudPlayerData> loadEntries();
}
