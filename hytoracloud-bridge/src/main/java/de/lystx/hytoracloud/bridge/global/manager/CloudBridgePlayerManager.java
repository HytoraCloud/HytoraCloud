package de.lystx.hytoracloud.bridge.global.manager;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.ObjectCloudPlayerManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;


@Setter @Getter
public class CloudBridgePlayerManager implements ObjectCloudPlayerManager {

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
            e.printStackTrace();
        }
    }

    @Override
    public DriverQuery<ICloudPlayer> getObjectSync(UUID uniqueId) {

        DriverRequest<ICloudPlayer> request = DriverRequest.create("PLAYER_GET_SYNC_UUID", "CLOUD", ICloudPlayer.class);
        request.append("uniqueId", uniqueId);

        return request.execute();
    }



    @Override
    public DriverQuery<ICloudPlayer> getObjectSync(String name) {
        DriverRequest<ICloudPlayer> request = DriverRequest.create("PLAYER_GET_SYNC_NAME", "CLOUD", ICloudPlayer.class);
        request.append("name", name);

        return request.execute();
    }

    @Override
    public void getObjectAsync(String name, Consumer<ICloudPlayer> consumer) {
        CloudDriver.getInstance().getExecutorService().execute(() -> consumer.accept(this.getObjectSync(name).pullValue()));
    }

    @Override
    public void getObjectAsync(UUID uniqueId, Consumer<ICloudPlayer> consumer) {
        CloudDriver.getInstance().getExecutorService().execute(() -> consumer.accept(this.getObjectSync(uniqueId).pullValue()));
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
