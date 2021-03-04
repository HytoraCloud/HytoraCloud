package de.lystx.cloudsystem.library.service.permission.impl;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerPermissionGroupAddEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerPermissionGroupRemoveEvent;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInPermissionPool;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.player.impl.DefaultCloudPlayerData;
import de.lystx.cloudsystem.library.service.util.Constants;
import de.lystx.cloudsystem.library.service.uuid.UUIDService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class PermissionPool implements Serializable {

    private List<PermissionGroup> permissionGroups;
    private List<CloudPlayerData> playerCache;
    private final SimpleDateFormat format;
    private final CloudLibrary cloudLibrary;
    private boolean enabled;

    public PermissionPool(CloudLibrary cloudLibrary) {
        this.cloudLibrary = cloudLibrary;
        this.enabled = true;
        this.permissionGroups = new LinkedList<>();
        this.playerCache = new LinkedList<>();
        this.format = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss", Locale.GERMAN);
    }

    /**
     * Checks if pool is available
     * @return boolean
     */
    public boolean isAvailable() {
        return (!this.permissionGroups.isEmpty() || !this.playerCache.isEmpty());
    }

    /**
     * Updates the permissionPool
     */
    public void update() {
        Constants.EXECUTOR.sendPacket(new PacketPlayInPermissionPool(this));
    }

    /**
     * Adds or removes a permission from a player
     * @param name
     * @param permission
     * @param add
     */
    public void updatePermissionEntry(String name, String permission, boolean add) {
        CloudPlayerData data = this.getPlayerData(name);
        if (data == null) {
            return;
        }
        this.playerCache.remove(data);
        List<String> permissions = data.getPermissions();
        if (add) {
            permissions.add(permission);
        } else {
            permissions.remove(permission);
        }
        data.setPermissions(permissions);
        this.playerCache.add(data);
    }

    /**
     * Updates Playerdata
     * @param playerName
     * @param newData
     */
    public void updatePlayerData(String playerName, CloudPlayerData newData) {
        CloudPlayerData oldData = this.getPlayerData(playerName);
        if (oldData == null) {
            return;
        }
        this.playerCache.set(this.playerCache.indexOf(oldData), newData);
    }

    /**
     * Checks for lowest ID if none is found returns DefaultPermissionGroup.class
     * @return default permissionGroup
     */
    public PermissionGroup getDefaultPermissionGroup() {
        this.permissionGroups.sort(Comparator.comparingInt(PermissionGroup::getId));
        try {
            if (!this.permissionGroups.isEmpty()) {
                return this.permissionGroups.get(this.permissionGroups.size() - 1);
            }
        } catch (Exception e) {
            //Ignoring
        }
        return new DefaultPermissionGroup();
    }

    /**
     * Checks if rank of player is valid
     * @param playerName
     * @param permissionGroup
     * @return valid
     */
    public boolean isRankValid(String playerName, PermissionGroup permissionGroup) {
        CloudPlayerData data = this.getPlayerData(playerName);
        if (data == null) {
            return false;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(this.format.toPattern());
        String bis = data.getForGroup(permissionGroup.getName()).getValidTime();
        if (bis.equalsIgnoreCase("lifetime") || bis.trim().isEmpty()) {
            return true;
        }
        try {
            Date date1 = new Date();
            Date date2 = this.format.parse(bis);
            long diffInMillies = date2.getTime() - date1.getTime();
            long d =  TimeUnit.MILLISECONDS.convert(diffInMillies,TimeUnit.MILLISECONDS);
            return (d >= 1);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Removes a group from a player
     * @param playerName
     * @param group
     */
    public void removePermissionGroup(String playerName, PermissionGroup group) {
        CloudPlayerData data = this.getPlayerDataOrDefault(playerName);
        if (data == null) {
            return;
        }
        this.playerCache.remove(data);
        List<PermissionEntry> permissionEntries = data.getPermissionEntries();
        PermissionEntry permissionEntry = data.getForGroup(group.getName());
        permissionEntries.remove(permissionEntry);
        data.setPermissionEntries(permissionEntries);
        this.playerCache.add(data);
        if (cloudLibrary != null) {
            cloudLibrary.getService(EventService.class).callEvent(new CloudPlayerPermissionGroupRemoveEvent(playerName, group));
        }
    }

    /**
     * Gives a permission to a group or removes a permission
     * @param permissionGroup
     * @param permission
     * @param add
     */
    public void updatePermissionGroupEntry(PermissionGroup permissionGroup, String permission, boolean add) {
        PermissionGroup group = this.getPermissionGroupFromName(permissionGroup.getName());
        if (add) {
            group.getPermissions().add(permission);
        } else {
            group.getPermissions().remove(permission);
        }
        this.permissionGroups.set(this.permissionGroups.indexOf(group), group);
    }

    /**
     * Updates a permissionGroup
     * @param permissionGroup
     */
    public void updatePermissionGroup(PermissionGroup permissionGroup) {
        PermissionGroup group = this.getPermissionGroupFromName(permissionGroup.getName());
        this.permissionGroups.set(this.permissionGroups.indexOf(group), permissionGroup);
        this.update();
    }

    /**
     * Gives a player a permissionGroup
     * @param playerName
     * @param group
     * @param i
     * @param validality
     */
    public void updatePermissionGroup(String playerName, PermissionGroup group, Integer i, PermissionValidality validality) {
        CloudPlayerData data = this.getPlayerDataOrDefault(playerName);
        if (data == null) {
            return;
        }
        this.playerCache.remove(data);
        List<PermissionEntry> permissionEntries = new LinkedList<>(data.getPermissionEntries());
        PermissionEntry entry = new PermissionEntry(this.tryUUID(playerName), group.getName(), "");
        permissionEntries.removeIf(permissionEntry -> permissionEntry.getPermissionGroup().equalsIgnoreCase(entry.getPermissionGroup()));

        permissionEntries.add(entry);
        if (i > 0) {
            Calendar c = Calendar.getInstance();
            try{
                c.setTime(format.parse(this.format.format(new Date())));
            } catch(ParseException ignored){}
            int i1 = Calendar.SECOND;
            if (validality.equals(PermissionValidality.MINUTE)) {
                i1 = Calendar.MINUTE;
            } else if (validality.equals(PermissionValidality.HOUR)) {
                i1 = Calendar.HOUR;
            } else if (validality.equals(PermissionValidality.DAY)) {
                i1 = Calendar.DAY_OF_MONTH;
            } else if (validality.equals(PermissionValidality.WEEK)) {
                i1 = Calendar.WEEK_OF_MONTH;
            } else if (validality.equals(PermissionValidality.MONTH)) {
                i1 = Calendar.MONTH;
            } else if (validality.equals(PermissionValidality.YEAR)) {
                i1 = Calendar.YEAR;
            }
            c.add(i1, i);
            entry.setValidTime(this.format.format(c.getTime()));
        }
        data.setPermissionEntries(permissionEntries);
        this.playerCache.add(data);
        if (cloudLibrary != null) {
            cloudLibrary.getService(EventService.class).callEvent(new CloudPlayerPermissionGroupAddEvent(playerName, group, i, validality));
        }
    }

    /**
     * Checks if player has entries
     * @param player
     */
    public void checkFix(String player) {
        CloudPlayerData data = this.getPlayerData(player);
        if (data != null) {
            if (data.getPermissionEntries().isEmpty()) {
                data.getPermissionEntries().add(new PermissionEntry(tryUUID(player), getDefaultPermissionGroup().getName(), ""));
                this.updatePlayerData(player, data);
                this.update();
            }
        }
    }

    /**
     * Returns all groups of player
     * @param player
     * @return
     */
    public List<PermissionGroup> getPermissionGroups(String player) {
        List<PermissionGroup> permissionGroups = new LinkedList<>();
        try {
            CloudPlayerData data = this.getPlayerData(player);
            if (data != null) {
                for (PermissionEntry permissionEntry : data.getPermissionEntries()) {
                    PermissionGroup permissionGroup = this.getPermissionGroupFromName(permissionEntry.getPermissionGroup());
                    permissionGroups.add(permissionGroup);
                }
            }
        } catch (NullPointerException e) {
            //Ignoring
        }
        return permissionGroups;
    }

    /**
     * Returns highest Group of player
     * @param player
     * @return (group sorted by IDS)
     */
    public PermissionGroup getHighestPermissionGroup(String player) {
        List<PermissionGroup> list = this.getPermissionGroups(player);
        list.sort(Comparator.comparingInt(PermissionGroup::getId));
        if (list.isEmpty()) {
            return this.getDefaultPermissionGroup();
        } else {
            return list.get(0);
        }
    }

    /**
     * Data | Name
     * @param playerName
     * @return Players data
     */
    public CloudPlayerData getPlayerData(String playerName) {
        return this.playerCache.stream().filter(cloudPlayerData -> cloudPlayerData.getName().equalsIgnoreCase(playerName)).findFirst().orElse(null);
    }

    /**
     * Data | UUID
     * @param uuid
     * @return Players data
     */
    public CloudPlayerData getPlayerData(UUID uuid) {
        return this.playerCache.stream().filter(cloudPlayerData -> cloudPlayerData.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Data | Default
     * @param playerName
     * @return Data or default data
     */
    public CloudPlayerData getPlayerDataOrDefault(String playerName) {
        CloudPlayerData pre = this.getPlayerData(playerName);
        if (pre == null) {
            UUID uuid = this.tryUUID(playerName);
            CloudPlayerData data = new DefaultCloudPlayerData(uuid, playerName);
            data.setDefault(true);
            return data;
        } else {
            return pre;
        }
    }

    /**
     * Checks if a player has permision
     * @param playerName
     * @param permission
     * @return boolean
     */
    public boolean hasPermission(String playerName, String permission) {
        if (!this.enabled) {
            return true;
        }
        boolean is = false;
        CloudPlayerData data = this.getPlayerData(playerName);
        if (data != null) {
            if (data.getPermissions().contains(permission) || data.getPermissions().contains("*")) {
                is = true;
            }
            for (PermissionEntry permissionEntry : data.getPermissionEntries()) {
                PermissionGroup permissionGroup = this.getPermissionGroupFromName(permissionEntry.getPermissionGroup());
                if (permissionGroup != null) {
                    for (String p : permissionGroup.getPermissions()) {
                        if (p.equalsIgnoreCase(permission) || p.equalsIgnoreCase("*")) {
                            is = true;
                            break;
                        }
                    }
                    for (String i : permissionGroup.getInheritances()) {
                        PermissionGroup inheritance = this.getPermissionGroupFromName(i);
                        if (inheritance != null) {
                            for (String p : inheritance.getPermissions()) {
                                if (p.equalsIgnoreCase(permission) || p.equalsIgnoreCase("*")) {
                                    is = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return is;
    }

    /**
     * Streams through groups
     * @param name
     * @return group from Name
     */
    public PermissionGroup getPermissionGroupFromName(String name) {
        return this.permissionGroups.stream().filter(permissionGroup -> permissionGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Saves all groups and data to database
     * @param file
     * @param directory
     * @param database
     * @return object of file
     */
    public VsonObject save(File file, File directory, CloudDatabase database) {
        try {
            VsonObject document = new VsonObject(file, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            document.clear();
            for (PermissionGroup permissionGroup : this.permissionGroups) {
                document.append(permissionGroup.getName(), permissionGroup);
            }
            document.save();
            this.playerCache.forEach(cloudPlayerData -> database.setPlayerData(cloudPlayerData.getUuid(), cloudPlayerData));
            this.clearInvalidUUIDs(directory);
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Clearing invalid UUIDs from directory
     * @param directory
     */
    public void clearInvalidUUIDs(File directory) {
        new Thread(() -> {
            try {
                for (File file : Objects.requireNonNull(directory.listFiles())) {
                    try {
                        String uuid = file.getName().split("\\.")[0];
                        if (!isUUID(uuid) || UUIDService.getName(UUID.fromString(uuid)) == null) {
                            file.delete();
                        }
                    } catch (NullPointerException e) {
                        file.delete();
                    }
                }
            } catch (NullPointerException | IOException e) {
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
     * Gets UUID internal by name
     * @param name
     * @return
     */
    private UUID getUUID(String name) {
        CloudPlayerData cloudPlayerData = this.playerCache.stream().filter(data -> data.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (cloudPlayerData == null) {
            return null;
        }
        return cloudPlayerData.getUuid();
    }

    /**
     * Tries to get UUID by name
     * If its not cached from pool it gets from web
     * @param name
     * @return
     */
    public UUID tryUUID(String name) {
        return this.getUUID(name) == null ? UUIDService.getUUID(name) : this.getUUID(name);
    }

    /**
     * Tries to get UUID from IP
     * @param input
     * @return
     */
    public UUID fromIP(String input) {
        CloudPlayerData data = this.playerCache.stream().filter(cloudPlayerData -> cloudPlayerData.getIpAddress().equalsIgnoreCase(input)).findFirst().orElse(null);
        if (data == null) {
            return null;
        }
        return data.getUuid();
    }

    /**
     * Tries to get Name by UUID
     * If its not cached from pool it gets from web
     * @param uuid
     * @return
     */
    public String tryName(UUID uuid) {
        try {
            return this.getName(uuid) == null ? UUIDService.getName(uuid) : this.getName(uuid);
        } catch (IOException e) {
            return null;
        }
    }

    private String getName(UUID uuid) {
        CloudPlayerData cloudPlayerData = this.playerCache.stream().filter(data -> data.getUuid().equals(uuid)).findFirst().orElse(null);
        if (cloudPlayerData == null) {
            return null;
        }
        return cloudPlayerData.getName();
    }


    /**
     * CHecks if player is registered in Database
     * @param name
     * @return
     */
    public boolean isRegistered(String name) {
        return (this.getPlayerData(name) != null);
    }
}