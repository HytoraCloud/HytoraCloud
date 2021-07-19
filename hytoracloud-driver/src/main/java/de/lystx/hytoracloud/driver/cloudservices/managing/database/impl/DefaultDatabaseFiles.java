package de.lystx.hytoracloud.driver.cloudservices.managing.database.impl;

import utillity.JsonEntity;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.DatabaseType;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.IDatabase;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerInformation;

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
    public void registerPlayer(ICloudPlayer ICloudPlayer) {
        if (!this.isRegistered(ICloudPlayer.getUniqueId())) {
            PlayerInformation data = CloudDriver.getInstance().getPermissionPool().getDefaultPlayerInformation(ICloudPlayer.getUniqueId(), ICloudPlayer.getName(), ICloudPlayer.getIpAddress());
            this.saveOfflinePlayer(ICloudPlayer.getUniqueId(), data);
        } else {
            PlayerInformation playerInformation = this.getOfflinePlayer(ICloudPlayer.getUniqueId());
            PlayerInformation newData = new PlayerInformation(ICloudPlayer.getUniqueId(), ICloudPlayer.getName(), playerInformation.getPermissionEntries(), playerInformation.getExclusivePermissions(), ICloudPlayer.getIpAddress(), playerInformation.isNotifyServerStart(), playerInformation.getFirstLogin(), playerInformation.getLastLogin());
            this.saveOfflinePlayer(ICloudPlayer.getUniqueId(), newData);
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
