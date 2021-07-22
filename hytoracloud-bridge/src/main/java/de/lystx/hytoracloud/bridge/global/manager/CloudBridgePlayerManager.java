package de.lystx.hytoracloud.bridge.global.manager;

import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestPlayerNamed;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestPlayerUniqueId;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.response.Response;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;


@Setter @Getter
public class CloudBridgePlayerManager implements ICloudPlayerManager {

    private List<ICloudPlayer> cachedObjects;

    public CloudBridgePlayerManager() {
        this.cachedObjects = new LinkedList<>();
    }

    @Override
    public void update(ICloudPlayer cloudPlayer) {
        ICloudPlayer cachedObject = this.getCachedObject(cloudPlayer.getName());
        if (cachedObject == null) {
            this.registerPlayer(cloudPlayer);
            return;
        }

        try {
            this.cachedObjects.set(cachedObjects.indexOf(cachedObject), cloudPlayer);
        } catch (IndexOutOfBoundsException e) {
            //Ignoring this one
        }
    }

    @Override
    public Response<ICloudPlayer> getObjectSync(UUID uniqueId) {

        PacketRequestPlayerUniqueId packet = new PacketRequestPlayerUniqueId(uniqueId);
        Component component = packet.toReply(CloudDriver.getInstance().getConnection());

        return new Response<ICloudPlayer>() {
            @Override
            public ICloudPlayer get() {
                return component.get("player");
            }

            @Override
            public Component getComponent() {
                return component;
            }

            @Override
            public ResponseStatus getStatus() {
                return ResponseStatus.SUCCESS;
            }
        };
    }

    @Override
    public Response<ICloudPlayer> getObjectSync(String name) {
        PacketRequestPlayerNamed packet = new PacketRequestPlayerNamed(name);
        Component component = packet.toReply(CloudDriver.getInstance().getConnection());

        return new Response<ICloudPlayer>() {
            @Override
            public ICloudPlayer get() {
                return component.get("player");
            }

            @Override
            public Component getComponent() {
                return component;
            }

            @Override
            public ResponseStatus getStatus() {
                return ResponseStatus.SUCCESS;
            }
        };
    }

    @Override
    public void getObjectAsync(String name, Consumer<ICloudPlayer> consumer) {
        CloudDriver.getInstance().getExecutorService().execute(() -> consumer.accept(this.getObjectSync(name).get()));
    }

    @Override
    public void getObjectAsync(UUID uniqueId, Consumer<ICloudPlayer> consumer) {
        CloudDriver.getInstance().getExecutorService().execute(() -> consumer.accept(this.getObjectSync(uniqueId).get()));
    }

    @Override
    public List<OfflinePlayer> getOfflinePlayers() {
        return CloudDriver.getInstance().getPermissionPool().getCachedObjects();
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String name) {
        return getOfflinePlayers().stream().filter(cloudPlayerData -> cloudPlayerData.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public OfflinePlayer getOfflinePlayer(UUID uniqueId) {
        return getOfflinePlayers().stream().filter(cloudPlayerData -> cloudPlayerData.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    public ICloudPlayer getCachedObject(String name) {
        return this.cachedObjects.stream().filter(cloudPlayer -> cloudPlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public ICloudPlayer getCachedObject(UUID uuid) {
        return this.cachedObjects.stream().filter(cloudPlayer -> cloudPlayer.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }


    @NotNull
    @Override
    public Iterator<ICloudPlayer> iterator() {
        return this.cachedObjects.iterator();
    }

    @Override
    public void unregisterPlayer(ICloudPlayer player) {
        this.cachedObjects.removeIf(cloudPlayer -> cloudPlayer.getName().equalsIgnoreCase(player.getName()));
    }

    @Override
    public void registerPlayer(ICloudPlayer cloudPlayer) {
        if (this.getCachedObject(cloudPlayer.getName()) == null) {
            this.cachedObjects.add(cloudPlayer);
        }
    }
}
