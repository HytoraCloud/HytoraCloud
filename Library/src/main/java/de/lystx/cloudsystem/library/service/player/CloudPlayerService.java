package de.lystx.cloudsystem.library.service.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;

import java.util.*;

public class CloudPlayerService extends CloudService {

    private final List<CloudPlayer> cloudPlayers;
    private final CloudDatabase database;

    public CloudPlayerService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.cloudPlayers = new LinkedList<>();
        this.database = this.getCloudLibrary().getService(DatabaseService.class).getDatabase();
    }

    public boolean registerPlayer(CloudPlayer cloudPlayer) {
        this.cloudPlayers.add(cloudPlayer);
        boolean registered = this.database.isRegistered(cloudPlayer.getUuid());
        this.database.registerPlayer(cloudPlayer);
        return registered;
    }


    public CloudPlayerData getPlayerData(UUID uuid) {
        return this.database.getPlayerData(uuid);
    }

    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        this.database.setPlayerData(uuid, data);
    }

    public void removePlayer(CloudPlayer cloudPlayer) {
        try {
            CloudPlayerData data = this.getPlayerData(cloudPlayer.getUuid());
            data.setLastLogin(new Date().getTime());
            this.setPlayerData(cloudPlayer.getUuid(), data);
        } catch (NullPointerException e) {}
        this.cloudPlayers.remove(this.getOnlinePlayer(cloudPlayer.getName()));
    }

    public CloudPlayer getOnlinePlayer(String name) {
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getName().equalsIgnoreCase(name)) {
                return cloudPlayer;
            }
        }
        return null;
    }

    public CloudPlayer getOnlinePlayer(UUID uuid) {
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if (cloudPlayer.getUuid().equals(uuid)) {
                return cloudPlayer;
            }
        }
        return null;
    }

    public List<CloudPlayer> getOnlinePlayers() {
        return this.cloudPlayers;
    }

}

