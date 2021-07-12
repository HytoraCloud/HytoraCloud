package de.lystx.hytoracloud.launcher.cloud.impl.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.IDatabase;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.*;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
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
public class CloudSidePlayerManager implements ICloudService, PacketHandler, ICloudPlayerManager {

    private List<ICloudPlayer> onlinePlayers;
    private final IDatabase database;

    public CloudSidePlayerManager() {
        this.onlinePlayers = new LinkedList<>();
        this.database = this.getDriver().getDatabaseManager().getDatabase();

        CloudDriver.getInstance().executeIf(() -> CloudDriver.getInstance().getConnection().registerPacketHandler(CloudSidePlayerManager.this), () -> CloudDriver.getInstance().getConnection() != null);
    }

    @Override
    public void registerPlayer(ICloudPlayer ICloudPlayer) {
        if (this.getCachedPlayer(ICloudPlayer.getName()) != null) {
            return;
        }
        this.onlinePlayers.add(ICloudPlayer);
        boolean registered = this.database.isRegistered(ICloudPlayer.getUniqueId());
        this.database.registerPlayer(ICloudPlayer);
        this.clearDoubles();
        if (!registered) {
            CloudDriver.getInstance().getPermissionPool().update();
        }
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
     * @param ICloudPlayer
     */
    public void unregisterPlayer(ICloudPlayer ICloudPlayer) {
        try {
            PlayerInformation data = this.getPlayerData(ICloudPlayer.getUniqueId());
            data.setLastLogin(new Date().getTime());
            ICloudPlayer.setInformation(data);
            this.database.saveOfflinePlayer(ICloudPlayer.getUniqueId(), data);
            if (this.getDriver().getParent().getWebServer() == null) {
                return;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.onlinePlayers.remove(ICloudPlayer);
    }

    @Override
    public void getAsync(String name, Consumer<ICloudPlayer> consumer) {
        consumer.accept(getCachedPlayer(name));
    }

    @Override
    public void getAsync(UUID uniqueId, Consumer<ICloudPlayer> consumer) {
        consumer.accept(getCachedPlayer(uniqueId));
    }

    @Override
    public void update(ICloudPlayer ICloudPlayer) {
        try {
            ICloudPlayer cachedPlayer = getCachedPlayer(ICloudPlayer.getName());
            if (cachedPlayer != null) {
                onlinePlayers.remove(cachedPlayer);
                onlinePlayers.remove(ICloudPlayer);
            } else {
                this.registerPlayer(ICloudPlayer);
            }
            this.clearDoubles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearDoubles() {
        List<UUID> checked = new LinkedList<>();

        for (ICloudPlayer onlinePlayer : new LinkedList<>(this.onlinePlayers)) {
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
    public ICloudPlayer getCachedPlayer(String name) {
        return this.onlinePlayers.stream().filter(cloudPlayer -> cloudPlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Streams through all online players
     *
     * @param uuid
     * @return CloudPlayer by UUID
     */
    public ICloudPlayer getCachedPlayer(UUID uuid) {
        return this.onlinePlayers.stream().filter(cloudPlayer -> cloudPlayer.getUniqueId() == uuid).findFirst().orElse(null);
    }

    @Override
    public ICloudPlayer getPlayer(String name) {
        return this.getCachedPlayer(name);
    }

    @Override
    public ICloudPlayer getPlayer(UUID uniqueId) {
        return this.getCachedPlayer(uniqueId);
    }

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketUnregisterPlayer) {

            PacketUnregisterPlayer player = (PacketUnregisterPlayer)packet;
            ICloudPlayer ICloudPlayer = this.getCachedPlayer(player.getName());
            if (ICloudPlayer != null) {
                this.unregisterPlayer(ICloudPlayer);
                CloudDriver.getInstance().reload();
            }

        }
    }


    /**
     * Returns all {@link ICloudPlayer}s from a
     * ServiceGroup by Name
     * @param IServiceGroup the group
     * @return
     */
    public List<ICloudPlayer> getPlayersOnGroup(IServiceGroup IServiceGroup) {
        List<ICloudPlayer> list = new LinkedList<>();
        for (ICloudPlayer ICloudPlayer : this.onlinePlayers) {
            if (ICloudPlayer.getService() == null) {
                continue;
            }
            if (ICloudPlayer.getService().getGroup().getName().equalsIgnoreCase(IServiceGroup.getName())) {
                list.add(ICloudPlayer);
            }
        }
        return list;
    }

    /**
     * Returns {@link ICloudPlayer}s on a {@link IService}
     * @param IService the service
     * @return
     */
    public List<ICloudPlayer> getPlayersOnServer(IService IService) {
        List<ICloudPlayer> list = new LinkedList<>();
        for (ICloudPlayer ICloudPlayer : this.onlinePlayers) {
            if (ICloudPlayer.getService() == null) {
                continue;
            }
            if (ICloudPlayer.getService().getName().equalsIgnoreCase(IService.getName())) {
                list.add(ICloudPlayer);
            }
        }
        return list;
    }


    @NotNull
    @Override
    public Iterator<ICloudPlayer> iterator() {
        return this.onlinePlayers.iterator();
    }

    @Override
    public void reload() {
    }

    @Override
    public void save() {

    }

}

