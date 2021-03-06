package de.lystx.hytoracloud.driver.cloudservices.managing.permission;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.IDatabase;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerInformation;
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
    private boolean loaded;

    public PermissionService() {

        this.file = CloudDriver.getInstance().getInstance(FileService.class).getPermissionsFile();
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
        CloudDriver.getInstance().getPermissionPool().setCachedCloudPlayers(CloudDriver.getInstance().getDatabaseManager().getDatabase().loadEntries());
        if (!loaded) {
            loaded = true;
            if (!CloudDriver.getInstance().getNetworkConfig().isSetupDone()) {
                return;
            }
            CloudDriver.getInstance().getParent().getConsole().sendMessage("DATABASE", "§7Loaded §b" + CloudDriver.getInstance().getPermissionPool().getCachedCloudPlayers().size() + " PlayerEntries §ffrom Database §h[§7Type: §b" + CloudDriver.getInstance().getDatabaseManager().getDatabase().getType().name() + "§h]!");
        }
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

        JsonEntity jsonEntity = new JsonEntity(file);
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
            jsonEntity.append("enabled", true);
            jsonEntity.append(defaultGroup.getName(), defaultGroup);
            jsonEntity.append(adminGroup.getName(), adminGroup);
            jsonEntity.save();
        }



        List<PermissionGroup> groups = new ArrayList<>();
        for (String key : jsonEntity.keys()) {
            if (key.equalsIgnoreCase("enabled")) {
                CloudDriver.getInstance().getPermissionPool().setEnabled(jsonEntity.getBoolean(key));
                continue;
            }
            PermissionGroup group = jsonEntity.getObject(key, PermissionGroup.class);
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
            JsonEntity jsonEntity = new JsonEntity(file);
            jsonEntity.append("enabled", CloudDriver.getInstance().getPermissionPool().isEnabled()); //If Enabled

            //Saves PermissionGroups
            for (PermissionGroup permissionGroup : new LinkedList<>(CloudDriver.getInstance().getPermissionPool().getCachedPermissionGroups())) {
                jsonEntity.append(permissionGroup.getName(), permissionGroup);
            }
            jsonEntity.save(); //Saves all the perms.json

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
