package de.lystx.cloudsystem.library.service.database;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;

import java.util.UUID;

public interface CloudDatabase {

    void connect();

    void disconnect();

    void registerPlayer(CloudPlayer cloudPlayer);

    boolean isRegistered(UUID uuid);

    boolean isConnected();

    CloudPlayerData getPlayerData(UUID uuid);

    void setPlayerData(UUID uuid, CloudPlayerData data);
}
