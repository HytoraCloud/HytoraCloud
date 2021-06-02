package de.lystx.hytoracloud.driver.service.permission;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.database.IDatabase;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerInformation;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;

@Getter @Setter
@ICloudServiceInfo(
        name = "PermissionService",
        type = CloudServiceType.MANAGING,
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

        this.file = CloudDriver.getInstance().getInstance(FileService.class).getPermissionsFile();
        this.reload();
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
        CloudDriver.getInstance().getPermissionPool().setCachedCloudPlayers(CloudDriver.getInstance().getDatabaseManager().getDatabase().loadEntries());
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

        JsonBuilder jsonBuilder = new JsonBuilder(file);
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
            jsonBuilder.append("enabled", true);
            jsonBuilder.append(defaultGroup.getName(), defaultGroup);
            jsonBuilder.append(adminGroup.getName(), adminGroup);
            jsonBuilder.save();
        }



        List<PermissionGroup> groups = new ArrayList<>();
        for (String key : jsonBuilder.keys()) {
            if (key.equalsIgnoreCase("enabled")) {
                CloudDriver.getInstance().getPermissionPool().setEnabled(jsonBuilder.getBoolean(key));
                continue;
            }
            PermissionGroup group = jsonBuilder.getObject(key, PermissionGroup.class);
            groups.add(group);
        }

        CloudDriver.getInstance().getPermissionPool().setCachedPermissionGroups(groups);

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
     * @param database the databsae to save data (Files, MySQL, MongoDB)
     */
    public void save(File file, File directory, IDatabase database) {

        try {
            if (CloudDriver.getInstance().getPermissionPool().getCachedPermissionGroups().isEmpty()) {
                this.ignore = true;
                this.loadGroups();
            }
            JsonBuilder jsonBuilder = new JsonBuilder(file);
            jsonBuilder.append("enabled", CloudDriver.getInstance().getPermissionPool().isEnabled()); //If Enabled

            //Saves PermissionGroups
            for (PermissionGroup permissionGroup : new LinkedList<>(CloudDriver.getInstance().getPermissionPool().getCachedPermissionGroups())) {
                jsonBuilder.append(permissionGroup.getName(), permissionGroup);
            }
            jsonBuilder.save(); //Saves all the perms.json

            //Saves PlayerData
            if (database != null) {
                for (PlayerInformation playerInformation : new LinkedList<>(CloudDriver.getInstance().getPermissionPool().getCachedCloudPlayers())) {
                    if (playerInformation == null) {
                        continue;
                    }
                    database.saveOfflinePlayer(playerInformation.getUniqueId(), playerInformation);
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
        if (CloudDriver.getInstance().getInstance(FileService.class).getCloudPlayerDirectory() == null) {
            return;
        }
        this.save(this.file, this.getDriver().getInstance(FileService.class).getCloudPlayerDirectory(), getDriver().getDatabaseManager().getDatabase());
    }

}
