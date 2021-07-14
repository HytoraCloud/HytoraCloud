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
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.Response;
import net.hytora.networking.elements.packet.response.ResponseStatus;
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
public class CloudSidePlayerManager implements ICloudService, ICloudPlayerManager {

    private List<ICloudPlayer> cachedObjects;
    private final IDatabase database;

    public CloudSidePlayerManager() {
        this.cachedObjects = new LinkedList<>();
        this.database = this.getDriver().getDatabaseManager().getDatabase();

    }

    @Override
    public void registerPlayer(ICloudPlayer cloudPlayer) {
        if (this.getCachedObject(cloudPlayer.getName()) != null) {
            return;
        }
        this.cachedObjects.add(cloudPlayer);
        boolean registered = this.database.isRegistered(cloudPlayer.getUniqueId());
        this.database.registerPlayer(cloudPlayer);
        this.clearDoubles();
        if (!registered) {
            CloudDriver.getInstance().getPermissionPool().update();
        }
    }

    public PlayerInformation getPlayerData(UUID uuid) {
        return this.database.getOfflinePlayer(uuid);
    }

    public void setPlayerData(UUID uuid, PlayerInformation data) {
        this.database.saveOfflinePlayer(uuid, data);
    }

    @Override
    public void unregisterPlayer(ICloudPlayer cloudPlayer) {
        try {
            PlayerInformation data = this.getPlayerData(cloudPlayer.getUniqueId());
            data.setLastLogin(new Date().getTime());
            cloudPlayer.setInformation(data);
            this.database.saveOfflinePlayer(cloudPlayer.getUniqueId(), data);
            if (this.getDriver().getParent().getWebServer() == null) {
                return;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.cachedObjects.remove(cloudPlayer);
    }


    @Override
    public void update(ICloudPlayer cloudPlayer) {
        try {
            ICloudPlayer cachedPlayer = getCachedObject(cloudPlayer.getName());
            if (cachedPlayer != null) {
                cachedObjects.remove(cachedPlayer);
                cachedObjects.remove(cloudPlayer);
            } else {
                this.registerPlayer(cloudPlayer);
            }
            this.clearDoubles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearDoubles() {
        List<UUID> checked = new LinkedList<>();

        for (ICloudPlayer onlinePlayer : new LinkedList<>(this.cachedObjects)) {
            if (checked.contains(onlinePlayer.getUniqueId())) {
                this.cachedObjects.remove(onlinePlayer);
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

    @Override
    public ICloudPlayer getCachedObject(String name) {
        return this.cachedObjects.stream().filter(cloudPlayer -> cloudPlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public ICloudPlayer getCachedObject(UUID uuid) {
        return this.cachedObjects.stream().filter(cloudPlayer -> cloudPlayer.getUniqueId() == uuid).findFirst().orElse(null);
    }

    @Override
    public void getObjectAsync(String name, Consumer<ICloudPlayer> consumer) {
        consumer.accept(this.getCachedObject(name));
    }

    @Override
    public void getObjectAsync(UUID uniqueId, Consumer<ICloudPlayer> consumer) {
        consumer.accept(this.getCachedObject(uniqueId));
    }

    @Override
    public Response<ICloudPlayer> getObjectSync(String name) {
        return new Response<ICloudPlayer>() {
            @Override
            public ICloudPlayer get() {
                return getCachedObject(name);
            }

            @Override
            public Component getComponent() {
                return new Component();
            }

            @Override
            public ResponseStatus getStatus() {
                return ResponseStatus.SUCCESS;
            }
        };
    }

    @Override
    public Response<ICloudPlayer> getObjectSync(UUID uniqueId) {
        return new Response<ICloudPlayer>() {
            @Override
            public ICloudPlayer get() {
                return getCachedObject(uniqueId);
            }

            @Override
            public Component getComponent() {
                return new Component();
            }

            @Override
            public ResponseStatus getStatus() {
                return ResponseStatus.SUCCESS;
            }
        };
    }

    @NotNull
    @Override
    public Iterator<ICloudPlayer> iterator() {
        return this.cachedObjects.iterator();
    }

    @Override
    public void reload() {
    }

    @Override
    public void save() {

    }

}

