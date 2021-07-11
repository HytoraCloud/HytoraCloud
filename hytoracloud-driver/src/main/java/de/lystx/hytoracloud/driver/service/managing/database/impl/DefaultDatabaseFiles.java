package de.lystx.hytoracloud.driver.service.managing.database.impl;

import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.service.managing.database.DatabaseType;
import de.lystx.hytoracloud.driver.service.managing.database.IDatabase;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.managing.player.impl.PlayerInformation;

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
    public void registerPlayer(CloudPlayer cloudPlayer) {
        if (!this.isRegistered(cloudPlayer.getUniqueId())) {
            PlayerInformation data = CloudDriver.getInstance().getPermissionPool().getDefaultPlayerInformation(cloudPlayer.getUniqueId(), cloudPlayer.getName(), cloudPlayer.getIpAddress());
            this.saveOfflinePlayer(cloudPlayer.getUniqueId(), data);
        } else {
            PlayerInformation playerInformation = this.getOfflinePlayer(cloudPlayer.getUniqueId());
            PlayerInformation newData = new PlayerInformation(cloudPlayer.getUniqueId(), cloudPlayer.getName(), playerInformation.getPermissionEntries(), playerInformation.getExclusivePermissions(), cloudPlayer.getIpAddress(), playerInformation.isNotifyServerStart(), playerInformation.getFirstLogin(), playerInformation.getLastLogin());
            this.saveOfflinePlayer(cloudPlayer.getUniqueId(), newData);
        }
    }

    @Override
    public PlayerInformation getOfflinePlayer(UUID uuid) {
        File dir = CloudDriver.getInstance().getInstance(FileService.class).getCloudPlayerDirectory();
        File file = new File(dir, uuid + ".json");
        JsonEntity jsonEntity = new JsonEntity(file);
        return jsonEntity.getAs(PlayerInformation.class);
    }

    @Override
    public void saveOfflinePlayer(UUID uuid, PlayerInformation playerInformation) {
        playerInformation.setDefault(false);
        File dir = CloudDriver.getInstance().getInstance(FileService.class).getCloudPlayerDirectory();
        File file = new File(dir, uuid + ".json");
        JsonEntity jsonEntity = new JsonEntity(file);
        jsonEntity.clear();
        jsonEntity.append(playerInformation);
        jsonEntity.save();
    }

    @Override
    public List<PlayerInformation> loadEntries() {
        List<PlayerInformation> list = new LinkedList<>();
        File dir = CloudDriver.getInstance().getInstance(FileService.class).getCloudPlayerDirectory();
        for (File listFile : Objects.requireNonNull(dir.listFiles())) {
            JsonEntity jsonEntity = new JsonEntity(listFile);
            PlayerInformation playerData = jsonEntity.getAs(PlayerInformation.class);
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
