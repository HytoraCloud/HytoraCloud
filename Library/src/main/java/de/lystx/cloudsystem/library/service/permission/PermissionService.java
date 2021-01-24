package de.lystx.cloudsystem.library.service.permission;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.permission.impl.DefaultPermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Getter @Setter
public class PermissionService extends CloudService {

    private final File file;
    private PermissionPool permissionPool;

    public PermissionService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.file = cloudLibrary.getService(FileService.class).getPermissionsFile();
        this.permissionPool = new PermissionPool();

        this.load();
        this.loadEntries();
    }

    public void loadEntries() {
        File dir = getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory();
        for (File listFile : Objects.requireNonNull(dir.listFiles())) {
            Document document = Document.fromFile(listFile);
            CloudPlayerData playerData = document.getObject(document.getJsonObject(), CloudPlayerData.class);
            if (playerData == null) {
                continue;
            }
            PermissionGroup permissionGroup = this.permissionPool.getPermissionGroupFromName(playerData.getPermissionGroup());
            if (permissionGroup == null) {
                continue;
            }
            this.permissionPool.getPlayerCache().add(playerData);
        }
    }

    public void load() {
        this.permissionPool.getPlayerCache().clear();
        this.permissionPool.getPermissionGroups().clear();
        if (!this.file.exists()) {
            Document document = new Document();
            PermissionGroup defaultGroup = new DefaultPermissionGroup();
            PermissionGroup adminGroup = new PermissionGroup(
                    "Admin",
                    0,
                    "§4Admin §8┃ §7",
                    "§7",
                    "§7",
                    Arrays.asList(
                        "*",
                        "cloudsystem.group.maintenance",
                        "cloudsystem.network.maintenance",
                        "cloudsystem.command",
                        "cloudsystem.perms.command",
                        "cloudsystem.command.service",
                        "cloudsystem.command.whereis",
                        "cloudsystem.notify",
                        "bungeecord.command.alert",
                        "bungeecord.command.end",
                        "bungeecord.command.ip",
                        "bungeecord.command.reload",
                        "bungeecord.command.send",
                        "bungeecord.command.server",
                        "bungeecord.command.list"
                    ),
                    Collections.singletonList("Player")
            );
            document.append(defaultGroup.getName(), defaultGroup);
            document.append(adminGroup.getName(), adminGroup);
            document.save(this.file);
        }
        Document document = new Document(file);
        for (String key : document.keys()) {
            PermissionGroup group = document.getObject(key, PermissionGroup.class);
            this.permissionPool.getPermissionGroups().add(group);
        }
    }

    public void save() {
        if (this.permissionPool == null) {
            return;
        }
        if (this.file == null) {
            return;
        }
        if (this.getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory() == null) {
            return;
        }
        this.permissionPool.save(this.file, this.getCloudLibrary().getService(FileService.class).getCloudPlayerDirectory(), getCloudLibrary().getService(DatabaseService.class).getDatabase());
    }

}
