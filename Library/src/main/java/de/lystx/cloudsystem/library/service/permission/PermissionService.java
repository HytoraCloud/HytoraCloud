package de.lystx.cloudsystem.library.service.permission;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class PermissionService extends CloudService {

    private final File file;
    private PermissionPool permissionPool;
    private boolean enabled;

    public PermissionService(CloudLibrary cloudLibrary, String name, CloudServiceType cloudType) {
        super(cloudLibrary, name, cloudType);
        this.file = cloudLibrary.getService(FileService.class).getPermissionsFile();
        this.enabled = true;
        this.permissionPool = new PermissionPool(cloudLibrary);

        this.load();
        this.loadEntries();
    }

    /**
     * Loads entries from database
     */
    public void loadEntries() {
        if (!getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        try {
            if (this.getCloudLibrary().getService(DatabaseService.class) == null) {
                return;
            }
            List<CloudPlayerData> list = this.getCloudLibrary().getService(DatabaseService.class).getDatabase().loadEntries();
            this.permissionPool.getPlayerCache().addAll(list);
            this.permissionPool.setEnabled(this.enabled);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads groups
     */
    public void load() {
        if (!getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        this.permissionPool.getPlayerCache().clear();
        this.permissionPool.getPermissionGroups().clear();
        if (!this.file.exists()) {
            VsonObject vsonObject = new VsonObject(VsonSettings.SAFE_TREE_OBJECTS, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            PermissionGroup defaultGroup = new PermissionGroup("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new SerializableDocument());
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
                    Collections.singletonList(defaultGroup.getName()),
                    new SerializableDocument()
            );
            vsonObject.append("enabled", true);
            vsonObject.append(defaultGroup.getName(), defaultGroup);
            vsonObject.append(adminGroup.getName(), adminGroup);
            vsonObject.save(this.file);
            this.load();
            return;
        }
        try {
            VsonObject vsonObject = new VsonObject(file, VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            for (String key : vsonObject.keys()) {
                if (key.equalsIgnoreCase("enabled")) {
                    enabled = vsonObject.getBoolean(key);
                    this.permissionPool.setEnabled(enabled);
                    continue;
                }
                VsonObject permsGroup = vsonObject.getVson(key);
                if (!permsGroup.has("entries")) {
                    permsGroup.append("entries", new SerializableDocument());
                    vsonObject.append(key, permsGroup);
                    vsonObject.save();
                }
                PermissionGroup group = permsGroup.getAs(PermissionGroup.class);
                this.permissionPool.getPermissionGroups().add(group);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        permissionPool.update();
    }

    /**
     * Saves Pool and groups
     */
    public void save() {
        if (!getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
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
