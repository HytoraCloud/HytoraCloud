package de.lystx.cloudsystem.library.service.database.impl;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import lombok.Getter;

import java.io.File;
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
            document.appendAll(new CloudPlayerData(
                    cloudPlayer.getUuid(),
                    cloudPlayer.getName(),
                    "Player",
                    "Player",
                    "",
                    new LinkedList<>(),
                    cloudPlayer.getIpAddress(),
                    true,
                    new Date().getTime(),
                    0L
            ));
            document.save();
        } else {
            Document document = new Document(file);
            CloudPlayerData cloudPlayerData = document.getObject(document.getJsonObject(), CloudPlayerData.class);
            CloudPlayerData newData = new CloudPlayerData(cloudPlayer.getUuid(), cloudPlayer.getName(), cloudPlayerData.getPermissionGroup(), cloudPlayerData.getTempPermissionGroup(), cloudPlayerData.getValidadilityTime(), cloudPlayerData.getPermissions(), cloudPlayer.getIpAddress(), cloudPlayerData.isNotifyServerStart(), cloudPlayerData.getFirstLogin(), cloudPlayerData.getLastLogin());
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
