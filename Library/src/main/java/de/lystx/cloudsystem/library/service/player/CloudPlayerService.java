package de.lystx.cloudsystem.library.service.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerJoinEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerQuitEvent;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.util.*;

@Getter
public class CloudPlayerService extends CloudService {

    private final List<CloudPlayer> cloudPlayers;
    private final CloudDatabase database;

    public CloudPlayerService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.cloudPlayers = new LinkedList<>();
        this.database = this.getCloudLibrary().getService(DatabaseService.class).getDatabase();
        if (this.getCloudLibrary().getWebServer() == null) {
            return;
        }
        cloudLibrary.getWebServer().update("players", this.toDocument());
    }

    public boolean registerPlayer(CloudPlayer cloudPlayer) {
        this.getCloudLibrary().getService(EventService.class).callEvent(new CloudPlayerJoinEvent(cloudPlayer));
        this.cloudPlayers.add(cloudPlayer);
        boolean registered = this.database.isRegistered(cloudPlayer.getUniqueId());
        this.database.registerPlayer(cloudPlayer);
        if (this.getCloudLibrary().getWebServer() != null) {
            this.getCloudLibrary().getWebServer().update("players", this.toDocument());
        }
        return registered;
    }

    public VsonObject toDocument() {
        return new VsonObject().append("players", this.cloudPlayers);
    }

    public CloudPlayerData getPlayerData(UUID uuid) {
        return this.database.getPlayerData(uuid);
    }

    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        this.database.setPlayerData(uuid, data);
    }

    public void removePlayer(CloudPlayer cloudPlayer) {
        try {
            CloudPlayerData data = this.getPlayerData(cloudPlayer.getUniqueId());
            data.setLastLogin(new Date().getTime());
            this.setPlayerData(cloudPlayer.getUniqueId(), data);
            getCloudLibrary().getService(EventService.class).callEvent(new CloudPlayerQuitEvent(cloudPlayer));
            if (this.getCloudLibrary().getWebServer() == null) {
                return;
            }
            this.getCloudLibrary().getWebServer().update("players", this.toDocument());
        } catch (NullPointerException e) {}
        this.cloudPlayers.remove(this.getOnlinePlayer(cloudPlayer.getName()));
    }


    public void update(String name, CloudPlayer newPlayer) {
        CloudPlayer cloudPlayer = this.getOnlinePlayer(name);
        this.cloudPlayers.set(this.cloudPlayers.indexOf(cloudPlayer), newPlayer);
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
            if (cloudPlayer.getUniqueId().equals(uuid)) {
                return cloudPlayer;
            }
        }
        return null;
    }

    public List<CloudPlayer> getOnlinePlayers() {
        return this.cloudPlayers;
    }

}

