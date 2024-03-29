package de.lystx.hytoracloud.driver.player.permission;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.registry.ICloudService;
import de.lystx.hytoracloud.driver.registry.CloudServiceInfo;
import de.lystx.hytoracloud.driver.connection.database.IDatabase;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.player.required.OfflinePlayer;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;

@Getter @Setter
@CloudServiceInfo(
        name = "PermissionService",
        description = {
                "This service is used to manage the Permissions on the network",
                "And to read / save all permissionGroups and playerDatas!"
        },
        version = 1.5
)
public class PermissionService implements ICloudService {

    private final File file;
    private boolean ignore;

    public PermissionService() {

        this.file = CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getPermissionsFile();
        CloudDriver.getInstance().executeIf(this::reload, () -> CloudDriver.getInstance().getDatabaseManager() != null);
    }

    /**
     * Loads entries from database
     */
    public void reload() {
        if (!CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        this.loadGroups();
        if (CloudDriver.getInstance().getDatabaseManager() == null) {
            return;
        }
        CloudDriver.getInstance().getPermissionPool().setCachedObjects(CloudDriver.getInstance().getDatabaseManager().getDatabase().loadEntries());
    }

    /**
     * Clearing invalid UUIDs from directory
     * @param directory the directory to clear it in
     */
    public void clearInvalidUUIDs(File directory) {
        new Thread(() -> {
            try {
                for (File file : Objects.requireNonNull(directory.listFiles())) {
                    try {
                        String uuid = file.getName().split("\\.")[0];
                        if (!isUUID(uuid)) {
                            file.delete();
                        }
                    } catch (NullPointerException e) {
                        file.delete();
                    }
                }
            } catch (NullPointerException e) {
            }
        }, "async_uuid_clear_cache").start();
    }

    /**
     * Checks if something is uuid for clearingUUIDs
     * @param string
     * @return
     */
    private boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Loads groups
     */
    private void loadGroups() {
        if (!CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }

        JsonDocument jsonDocument = new JsonDocument(file);
        if (!this.file.exists()) {
            PermissionGroup defaultGroup = new PermissionGroup("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new HashMap<>());
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
                    new HashMap<>()
            );
            jsonDocument.append("enabled", true);
            jsonDocument.append(defaultGroup.getName(), defaultGroup);
            jsonDocument.append(adminGroup.getName(), adminGroup);
            jsonDocument.save();
        }



        List<PermissionGroup> groups = new ArrayList<>();
        for (String key : jsonDocument.keySet()) {
            if (key.equalsIgnoreCase("enabled")) {
                CloudDriver.getInstance().getPermissionPool().setEnabled(jsonDocument.getBoolean(key));
                continue;
            }
            PermissionGroup group = jsonDocument.get(key, PermissionGroup.class);
            groups.add(group);
        }

        CloudDriver.getInstance().getPermissionPool().setPermissionGroups(groups);

        if (!ignore) {
            CloudDriver.getInstance().getPermissionPool().update();
        } else {
            ignore = false;
        }
    }


    /**
     * Saves all groups and data to database
     *
     * @param file the file (perms.json)
     * @param directory the directory (player/)
     * @param database the database to save data (Files, MySQL, MongoDB)
     */
    public void save(File file, File directory, IDatabase database) {

        try {
            if (CloudDriver.getInstance().getPermissionPool().getPermissionGroups().isEmpty()) {
                this.ignore = true;
                this.loadGroups();
            }
            JsonDocument jsonDocument = new JsonDocument(file);
            jsonDocument.append("enabled", CloudDriver.getInstance().getPermissionPool().isEnabled()); //If Enabled

            //Saves PermissionGroups
            for (PermissionGroup permissionGroup : new LinkedList<>(CloudDriver.getInstance().getPermissionPool().getPermissionGroups())) {
                jsonDocument.append(permissionGroup.getName(), permissionGroup);
            }
            jsonDocument.save(); //Saves all the perms.json

            //Saves PlayerData
            if (database != null) {
                for (OfflinePlayer offlinePlayer : new LinkedList<>(CloudDriver.getInstance().getPermissionPool().getCachedObjects())) {
                    if (offlinePlayer == null) {
                        continue;
                    }
                    database.saveEntry(offlinePlayer.getUniqueId(), offlinePlayer);
                }
            }
            this.clearInvalidUUIDs(directory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves Pool and groups
     */
    public void save() {
        if (!CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        if (this.file == null) {
            return;
        }
        if (CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getCloudPlayerDirectory() == null) {
            return;
        }
        this.save(this.file, CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getCloudPlayerDirectory(), getDriver().getDatabaseManager().getDatabase());
    }

}
