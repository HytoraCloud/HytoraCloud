package de.lystx.hytoracloud.driver.cloudservices.managing.database.impl;

import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionEntry;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.DatabaseType;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.IDatabase;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;

import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;

import java.io.File;
import java.util.*;

@Getter
public class DefaultDatabaseFiles implements IDatabase {


    @Override
    public void connect() {}

    @Override
    public void disconnect() {}

    @Override
    public boolean isRegistered(UUID uuid) {
        return new File(CloudDriver.getInstance().getInstance(FileService.class).getCloudPlayerDirectory(), uuid + ".json").exists();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void createEntry(ICloudPlayer cloudPlayer) {
        if (!this.isRegistered(cloudPlayer.getUniqueId())) {


            OfflinePlayer data = new OfflinePlayer(cloudPlayer.getUniqueId(), cloudPlayer.getName(), Collections.singletonList(new PermissionEntry(CloudDriver.getInstance().getPermissionPool().getDefaultPermissionGroup().getName(), "")), new LinkedList<>(), cloudPlayer.getIpAddress(), true, true, new Date().getTime(), 0L, new HashMap<>());

            this.saveEntry(cloudPlayer.getUniqueId(), data);
        } else {
            OfflinePlayer offlinePlayer = this.getEntry(cloudPlayer.getUniqueId());
            OfflinePlayer newData = new OfflinePlayer(cloudPlayer.getUniqueId(), cloudPlayer.getName(), offlinePlayer.getPermissionEntries(), offlinePlayer.getExclusivePermissions(), cloudPlayer.getIpAddress(), offlinePlayer.isNotifyServerStart(), offlinePlayer.getFirstLogin(), offlinePlayer.getLastLogin());
            this.saveEntry(cloudPlayer.getUniqueId(), newData);
        }
    }

    @Override
    public OfflinePlayer getEntry(UUID uuid) {
        File dir = CloudDriver.getInstance().getInstance(FileService.class).getCloudPlayerDirectory();
        File file = new File(dir, uuid + ".json");
        JsonEntity jsonEntity = new JsonEntity(file);
        return jsonEntity.getAs(OfflinePlayer.class);
    }

    @Override
    public void saveEntry(UUID uuid, OfflinePlayer offlinePlayer) {
        offlinePlayer.setDefault(false);
        File dir = CloudDriver.getInstance().getInstance(FileService.class).getCloudPlayerDirectory();
        File file = new File(dir, uuid + ".json");
        JsonEntity jsonEntity = new JsonEntity(file);
        jsonEntity.clear();
        jsonEntity.append(offlinePlayer);
        jsonEntity.save();
    }

    @Override
    public List<OfflinePlayer> loadEntries() {
        List<OfflinePlayer> list = new LinkedList<>();
        File dir = CloudDriver.getInstance().getInstance(FileService.class).getCloudPlayerDirectory();
        for (File listFile : Objects.requireNonNull(dir.listFiles())) {
            JsonEntity jsonEntity = new JsonEntity(listFile);
            OfflinePlayer playerData = jsonEntity.getAs(OfflinePlayer.class);
            if (playerData == null) {
                continue;
            }
            list.add(playerData);
        }
        return list;
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.FILES;
    }

}
