package de.lystx.cloudsystem.library.service.permission;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.permission.impl.DefaultPermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class PermissionService extends CloudService {

    private final File file;
    private PermissionPool permissionPool;
    private boolean enabled;

    public PermissionService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.file = cloudLibrary.getService(FileService.class).getPermissionsFile();
        this.enabled = true;
        this.permissionPool = new PermissionPool(cloudLibrary);

        this.load();
        this.loadEntries();
    }

    public void loadEntries() {
        if (!getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            return;
        }
        try {
            List<CloudPlayerData> list = this.getCloudLibrary().getService(DatabaseService.class).getDatabase().loadEntries();
            this.permissionPool.getPlayerCache().addAll(list);
            this.permissionPool.setEnabled(this.enabled);
        } catch (NullPointerException e) {}
    }

    public void load() {
        if (!getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            return;
        }
        this.permissionPool.getPlayerCache().clear();
        this.permissionPool.getPermissionGroups().clear();
        if (!this.file.exists()) {
            VsonObject vsonObject = new VsonObject();
            PermissionGroup defaultGroup = new DefaultPermissionGroup();
            PermissionGroup adminGroup = new PermissionGroup(
                    "Admin",
                    0,
                    "§4Admin §8┃ §7",
                    "§7",
                    "§4",
                    "",
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
            vsonObject.append("enabled", true);
            vsonObject.append(defaultGroup.getName(), defaultGroup);
            vsonObject.append(adminGroup.getName(), adminGroup);
            vsonObject.save(this.file);
            this.load();
            return;
        }
        try {
            VsonObject vsonObject = new VsonObject(file);
            for (String key : vsonObject.keys()) {
                if (key.equalsIgnoreCase("enabled")) {
                    enabled = vsonObject.getBoolean(key);
                    this.permissionPool.setEnabled(enabled);
                    continue;
                }
                PermissionGroup group = vsonObject.getObject(key, PermissionGroup.class);
                this.permissionPool.getPermissionGroups().add(group);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (!getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            return;
        }
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
