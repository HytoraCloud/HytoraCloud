package de.lystx.cloudsystem.library.service.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.list.Filter;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.database.IDatabase;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.util.CloudCache;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.util.*;

@Getter
public class CloudPlayerService extends CloudService {

    private final List<CloudPlayer> onlinePlayers;
    private final IDatabase database;

    public CloudPlayerService(CloudLibrary cloudLibrary, String name, CloudServiceType type) {
        super(cloudLibrary, name, type);
        this.onlinePlayers = new LinkedList<>();
        this.database = this.getCloudLibrary().getService(DatabaseService.class).getDatabase();
        if (this.getCloudLibrary().getWebServer() == null) {
            return;
        }
        cloudLibrary.getWebServer().update("players", this.toDocument());
    }

    /**
     * Registers a player
     *
     * @param cloudPlayer
     * @return if player has joined before
     */
    public boolean registerPlayer(CloudPlayer cloudPlayer) {
        this.onlinePlayers.add(cloudPlayer);
        boolean registered = this.database.isRegistered(cloudPlayer.getUniqueId());
        this.database.registerPlayer(cloudPlayer);
        if (this.getCloudLibrary().getWebServer() != null) {
            this.getCloudLibrary().getWebServer().update("players", this.toDocument());
        }
        CloudCache.getInstance().setCloudPlayerFilter(new Filter<>(this.onlinePlayers));
        return registered;
    }

    /**
     * For WebServer
     *
     * @return VsonObject
     */
    public VsonObject toDocument() {
        return new VsonObject(VsonSettings.CREATE_FILE_IF_NOT_EXIST).append("players", this.onlinePlayers);
    }

    /**
     * @param uuid
     * @return Data of a player
     */
    public CloudPlayerData getPlayerData(UUID uuid) {
        return this.database.getPlayerData(uuid);
    }

    /**
     * Sets data of a player
     *
     * @param uuid
     * @param data
     */
    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        this.database.setPlayerData(uuid, data);
    }

    /**
     * Unregisters a player
     *
     * @param cloudPlayer
     */
    public void removePlayer(CloudPlayer cloudPlayer) {
        try {
            CloudPlayerData data = this.getPlayerData(cloudPlayer.getUniqueId());
            data.setLastLogin(new Date().getTime());
            this.setPlayerData(cloudPlayer.getUniqueId(), data);
            if (this.getCloudLibrary().getWebServer() == null) {
                return;
            }
            this.getCloudLibrary().getWebServer().update("players", this.toDocument());
        } catch (NullPointerException e) {
            //Ignoring because it doesn't break the cloud
        }
        this.onlinePlayers.remove(cloudPlayer);
        CloudCache.getInstance().setCloudPlayerFilter(new Filter<>(this.onlinePlayers));
    }

    /**
     * Updates a cloudPlayer
     *
     * @param name
     * @param newPlayer
     */
    public void update(String name, CloudPlayer newPlayer) {
        CloudPlayer cloudPlayer = this.getOnlinePlayer(name);
        if (cloudPlayer != null) {
            this.onlinePlayers.remove(cloudPlayer);
        }
        this.onlinePlayers.add(newPlayer);
        CloudCache.getInstance().setCloudPlayerFilter(new Filter<>(this.onlinePlayers));
    }

    /**
     * Streams through all online players
     *
     * @param name
     * @return CloudPlayer by name
     */
    public CloudPlayer getOnlinePlayer(String name) {
        return this.onlinePlayers.stream().filter(cloudPlayer -> cloudPlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Streams through all online players
     *
     * @param uuid
     * @return CloudPlayer by UUID
     */
    public CloudPlayer getOnlinePlayer(UUID uuid) {
        return this.onlinePlayers.stream().filter(cloudPlayer -> cloudPlayer.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }
}

