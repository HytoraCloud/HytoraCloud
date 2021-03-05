package de.lystx.cloudsystem.library.service.database.impl;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.player.impl.DefaultCloudPlayerData;
import de.lystx.cloudsystem.library.service.util.Constants;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class Files implements CloudDatabase {

    private final DatabaseService databaseService;

    public Files(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void connect() {}

    @Override
    public void disconnect() {}

    @Override
    public boolean isRegistered(UUID uuid) {
        return new File(this.databaseService.getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory(), uuid + ".json").exists();
    }

    @Override
    public boolean isConnected() {
        return true;
    }


    @Override
    public void registerPlayer(CloudPlayer cloudPlayer) {
        if (!this.isRegistered(cloudPlayer.getUniqueId())) {
            CloudPlayerData data = new DefaultCloudPlayerData(cloudPlayer.getUniqueId(), cloudPlayer.getName(), cloudPlayer.getIpAddress());
            this.setPlayerData(cloudPlayer.getUniqueId(), data);
        } else {
            CloudPlayerData cloudPlayerData = this.getPlayerData(cloudPlayer.getUniqueId());
            CloudPlayerData newData = new CloudPlayerData(cloudPlayer.getUniqueId(), cloudPlayer.getName(), cloudPlayerData.getPermissionEntries(), cloudPlayerData.getPermissions(), cloudPlayer.getIpAddress(), cloudPlayerData.isNotifyServerStart(), cloudPlayerData.getFirstLogin(), cloudPlayerData.getLastLogin());
            this.setPlayerData(cloudPlayer.getUniqueId(), newData);
        }
    }

    @Override
    public CloudPlayerData getPlayerData(UUID uuid) {
        File dir = this.databaseService.getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory();
        File file = new File(dir, uuid + ".json");
        try {
            VsonObject document = new VsonObject(file, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            return document.getAs(CloudPlayerData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        File dir = this.databaseService.getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory();
        File file = new File(dir, uuid + ".json");
        Document document = new Document(file);
        document.clear();
        document.append(data);
        document.save();
    }

    @Override
    public List<CloudPlayerData> loadEntries() {
        List<CloudPlayerData> list = new LinkedList<>();
        File dir = this.databaseService.getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory();
        for (File listFile : Objects.requireNonNull(dir.listFiles())) {
            try {
                VsonObject document = new VsonObject(listFile, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
                CloudPlayerData playerData = document.getAs(CloudPlayerData.class);
                if (playerData == null) {
                    continue;
                }
                list.add(playerData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

}
