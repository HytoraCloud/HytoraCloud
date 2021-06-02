package de.lystx.hytoracloud.launcher.cloud.impl.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerJoinCloudEvent;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerQuitCloudEvent;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.config.stats.StatsService;
import de.lystx.hytoracloud.driver.service.database.IDatabase;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.service.player.impl.*;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

@Getter
@Setter
@ICloudServiceInfo(
        name = "CloudPlayerService",
        type = CloudServiceType.MANAGING,
        description = {
                "This class manages all the CloudPlayers"
        },
        version = 1.4
)
public class DefaultICloudPlayerManager implements ICloudService, PacketHandler, ICloudPlayerManager {

    private List<CloudPlayer> onlinePlayers;
    private final IDatabase database;

    public DefaultICloudPlayerManager() {
        this.onlinePlayers = new LinkedList<>();
        this.database = this.getDriver().getDatabaseManager().getDatabase();

        CloudDriver.getInstance().executeIf(() -> CloudDriver.getInstance().getConnection().addPacketHandler(DefaultICloudPlayerManager.this), () -> CloudDriver.getInstance().getConnection() != null);
        if (this.getDriver().getParent().getWebServer() == null) {
            return;
        }
        CloudDriver.getInstance().getParent().getWebServer().update("players", this.toDocument());


    }

    /**
     * Registers a player
     *
     * @param cloudPlayer
     * @return if player has joined before
     */
    public void registerPlayer(CloudPlayer cloudPlayer) {
        if (this.getCachedPlayer(cloudPlayer.getName()) != null) {
            return;
        }
        this.onlinePlayers.add(cloudPlayer);
        boolean registered = this.database.isRegistered(cloudPlayer.getUniqueId());
        this.database.registerPlayer(cloudPlayer);
        if (this.getDriver().getParent().getWebServer() != null) {
            this.getDriver().getParent().getWebServer().update("players", this.toDocument());
        }

        CloudDriver.getInstance().callEvent(new CloudPlayerJoinCloudEvent(cloudPlayer));
        CloudDriver.getInstance().getInstance(StatsService.class).getStatistics().add("connections");

        this.clearDoubles();
        if (!registered) {
            CloudDriver.getInstance().getInstance(StatsService.class).getStatistics().add("registeredPlayers");
            CloudDriver.getInstance().getPermissionPool().update();
        }
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
    public PlayerInformation getPlayerData(UUID uuid) {
        return this.database.getOfflinePlayer(uuid);
    }

    /**
     * Sets data of a player
     *
     * @param uuid
     * @param data
     */
    public void setPlayerData(UUID uuid, PlayerInformation data) {
        this.database.saveOfflinePlayer(uuid, data);
    }

    /**
     * Unregisters a player
     *
     * @param cloudPlayer
     */
    public void unregisterPlayer(CloudPlayer cloudPlayer) {
        try {
            PlayerInformation data = this.getPlayerData(cloudPlayer.getUniqueId());
            data.setLastLogin(new Date().getTime());
            cloudPlayer.setPlayerInformation(data);
            this.database.saveOfflinePlayer(cloudPlayer.getUniqueId(), data);
            if (this.getDriver().getParent().getWebServer() == null) {
                return;
            }
            this.getDriver().getParent().getWebServer().update("players", this.toDocument());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.onlinePlayers.remove(cloudPlayer);
    }

    @Override
    public void getAsync(String name, Consumer<CloudPlayer> consumer) {
        consumer.accept(getCachedPlayer(name));
    }

    @Override
    public void getAsync(UUID uniqueId, Consumer<CloudPlayer> consumer) {
        consumer.accept(getCachedPlayer(uniqueId));
    }

    /**
     * Updates a cloudPlayer
     *
     * @param cloudPlayer the player
     */
    public void update(CloudPlayer cloudPlayer) {
        try {
            CloudPlayer cloudPlayer1 = getCachedPlayer(cloudPlayer.getName());
            if (cloudPlayer1 != null) {
                onlinePlayers.remove(cloudPlayer1);
                onlinePlayers.remove(cloudPlayer);
            }

            if (getCachedPlayer(cloudPlayer.getUniqueId()) == null) {
                this.registerPlayer(cloudPlayer);
            }
            clearDoubles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearDoubles() {
        List<UUID> checked = new LinkedList<>();

        for (CloudPlayer onlinePlayer : new LinkedList<>(this.onlinePlayers)) {
            if (checked.contains(onlinePlayer.getUniqueId())) {
                this.onlinePlayers.remove(onlinePlayer);
                continue;
            }
            checked.add(onlinePlayer.getUniqueId());
        }
    }

    @Override
    public List<PlayerInformation> getOfflinePlayers() {
        return CloudDriver.getInstance().getPermissionPool().getCachedCloudPlayers();
    }

    @Override
    public PlayerInformation getOfflinePlayer(String name) {
        return getOfflinePlayers().stream().filter(cloudPlayerData -> cloudPlayerData.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public PlayerInformation getOfflinePlayer(UUID uniqueId) {
        return getOfflinePlayers().stream().filter(cloudPlayerData -> cloudPlayerData.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    /**
     * Streams through all online players
     *
     * @param name
     * @return CloudPlayer by name
     */
    public CloudPlayer getCachedPlayer(String name) {
        return this.onlinePlayers.stream().filter(cloudPlayer -> cloudPlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Streams through all online players
     *
     * @param uuid
     * @return CloudPlayer by UUID
     */
    public CloudPlayer getCachedPlayer(UUID uuid) {
        return this.onlinePlayers.stream().filter(cloudPlayer -> cloudPlayer.getUniqueId() == uuid).findFirst().orElse(null);
    }

    @Override
    public CloudPlayer getPlayer(String name) {
        return this.getCachedPlayer(name);
    }

    @Override
    public CloudPlayer getPlayer(UUID uniqueId) {
        return this.getCachedPlayer(uniqueId);
    }

    @Override
    public void handle(Packet packet) {if (packet instanceof PacketUnregisterPlayer) {

            PacketUnregisterPlayer player = (PacketUnregisterPlayer)packet;
            CloudPlayer cloudPlayer = this.getCachedPlayer(player.getName());
            if (cloudPlayer != null) {
                CloudDriver.getInstance().callEvent(new CloudPlayerQuitCloudEvent(cloudPlayer));
                this.unregisterPlayer(cloudPlayer);
                CloudDriver.getInstance().reload();
            }

        }
    }


    /**
     * Returns all {@link CloudPlayer}s from a
     * ServiceGroup by Name
     * @param serviceGroup the group
     * @return
     */
    public List<CloudPlayer> getPlayersOnGroup(ServiceGroup serviceGroup) {
        List<CloudPlayer> list = new LinkedList<>();
        for (CloudPlayer cloudPlayer : this.onlinePlayers) {
            if (cloudPlayer.getService().getServiceGroup().getName().equalsIgnoreCase(serviceGroup.getName())) {
                list.add(cloudPlayer);
            }
        }
        return list;
    }

    /**
     * Returns {@link CloudPlayer}s on a {@link Service}
     * @param service the service
     * @return
     */
    public List<CloudPlayer> getPlayersOnServer(Service service) {
        List<CloudPlayer> list = new LinkedList<>();
        for (CloudPlayer cloudPlayer : this.onlinePlayers) {
            if (cloudPlayer.getService().getName().equalsIgnoreCase(service.getName())) {
                list.add(cloudPlayer);
            }
        }
        return list;
    }


    @NotNull
    @Override
    public Iterator<CloudPlayer> iterator() {
        return this.onlinePlayers.iterator();
    }
}

