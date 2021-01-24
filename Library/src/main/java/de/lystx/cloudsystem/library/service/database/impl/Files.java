package de.lystx.cloudsystem.library.service.database.impl;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.permission.impl.DefaultPermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.player.impl.DefaultCloudPlayerData;
import lombok.Getter;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

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
    public void registerPlayer(CloudPlayer cloudPlayer) {
        File dir = this.databaseService.getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory();
        File file = new File(dir, cloudPlayer.getUuid() + ".json");
        if (!file.exists()) {
            Document document = new Document(file);
            document.appendAll(new DefaultCloudPlayerData(cloudPlayer.getUuid(), cloudPlayer.getName()));
            document.save();
        } else {
            Document document = new Document(file);
            CloudPlayerData cloudPlayerData = document.getObject(document.getJsonObject(), CloudPlayerData.class);
            CloudPlayerData newData = new CloudPlayerData(cloudPlayer.getUuid(), cloudPlayer.getName(), cloudPlayerData.getPermissionEntries(), cloudPlayerData.getPermissions(), cloudPlayer.getIpAddress(), cloudPlayerData.isNotifyServerStart(), cloudPlayerData.getFirstLogin(), cloudPlayerData.getLastLogin());
            this.setPlayerData(cloudPlayer.getUuid(), newData);
        }
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        return new File(this.databaseService.getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory(), uuid + ".json").exists();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public CloudPlayerData getPlayerData(UUID uuid) {
        File dir = this.databaseService.getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory();
        File file = new File(dir, uuid + ".json");
        Document document = new Document(file);
        return document.getObject(document.getJsonObject(), CloudPlayerData.class);
    }

    @Override
    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        File dir = this.databaseService.getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory();
        File file = new File(dir, uuid + ".json");
        Document document = new Document(file);
        document.clear();
        document.appendAll(data);
        document.save();
    }

}
