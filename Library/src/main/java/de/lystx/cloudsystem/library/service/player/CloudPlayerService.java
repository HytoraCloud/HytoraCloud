package de.lystx.cloudsystem.library.service.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerJoinEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerQuitEvent;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import lombok.Getter;

import java.io.File;
import java.util.*;

@Getter
public class CloudPlayerService extends CloudService {

    private final List<CloudPlayer> cloudPlayers;
    private final Document properties;
    private final CloudDatabase database;

    public CloudPlayerService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.cloudPlayers = new LinkedList<>();
        this.properties = new Document(new File(cloudLibrary.getService(FileService.class).getDatabaseDirectory(), "cloudPlayerProperties.json"));
        this.database = this.getCloudLibrary().getService(DatabaseService.class).getDatabase();
        cloudLibrary.getWebServer().update("players", this.toDocument());
    }

    public boolean registerPlayer(CloudPlayer cloudPlayer) {
        this.getCloudLibrary().getService(EventService.class).callEvent(new CloudPlayerJoinEvent(cloudPlayer));
        this.cloudPlayers.add(cloudPlayer);
        boolean registered = this.database.isRegistered(cloudPlayer.getUuid());
        this.database.registerPlayer(cloudPlayer);
        this.getCloudLibrary().getWebServer().update("players", this.toDocument());
        return registered;
    }

    public Document toDocument() {
        return new Document().append("players", this.cloudPlayers);
    }

    public CloudPlayerData getPlayerData(UUID uuid) {
        return this.database.getPlayerData(uuid);
    }

    public Document getProperties(UUID uuid) {
        Document document = this.properties.getDocument(uuid.toString());
        if (document == null) {
            document = new Document();
            this.properties.append(uuid.toString(), document);
            this.properties.save();
        }
        return document;
    }



    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        this.database.setPlayerData(uuid, data);
    }

    public void removePlayer(CloudPlayer cloudPlayer) {
        try {
            CloudPlayerData data = this.getPlayerData(cloudPlayer.getUuid());
            data.setLastLogin(new Date().getTime());
            this.setPlayerData(cloudPlayer.getUuid(), data);
            getCloudLibrary().getService(EventService.class).callEvent(new CloudPlayerQuitEvent(cloudPlayer));
            this.getCloudLibrary().getWebServer().update("players", this.toDocument());
        } catch (NullPointerException e) {}
        this.cloudPlayers.remove(this.getOnlinePlayer(cloudPlayer.getName()));
    }


    public void update(String name, CloudPlayer newPlayer) {
        CloudPlayer cloudPlayer = this.getOnlinePlayer(name);
        this.cloudPlayers.set(this.cloudPlayers.indexOf(cloudPlayer), newPlayer);

        this.properties.append(newPlayer.getUuid().toString(), newPlayer.getProperties());
        this.properties.save();
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

