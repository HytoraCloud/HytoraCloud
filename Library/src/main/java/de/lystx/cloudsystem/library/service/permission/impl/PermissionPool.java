package de.lystx.cloudsystem.library.service.permission.impl;

import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInPermissionPool;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.player.impl.DefaultCloudPlayerData;
import de.lystx.cloudsystem.library.service.util.UUIDService;
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

    public PermissionPool() {
        this.permissionGroups = new LinkedList<>();
        this.playerCache = new LinkedList<>();
        this.format = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss", Locale.GERMAN);
    }

    public boolean isAvailable() {
        return (!this.permissionGroups.isEmpty() || !this.playerCache.isEmpty());
    }

    public void update(CloudClient connection) {
        connection.sendPacket(new PacketPlayInPermissionPool(this));
    }

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

    public void updatePlayerData(String playerName, CloudPlayerData newData) {
        CloudPlayerData oldData = this.getPlayerData(playerName);
        if (oldData == null) {
            return;
        }
        this.playerCache.set(this.playerCache.indexOf(oldData), newData);
    }

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
            return (this.getDateDiff(date1, date2) >= 1);
        } catch (ParseException e) {
            return false;
        }
    }

    private long getDateDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.MILLISECONDS.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public void removePermissionGroup(String playerName, PermissionGroup group) {
        try {
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
        } catch (Exception e) {}
    }

    public void updatePermissionGroup(String playerName, PermissionGroup group, Integer i, PermissionValidality validality) {
        CloudPlayerData data = this.getPlayerDataOrDefault(playerName);
        if (data == null) {
            return;
        }
        this.playerCache.remove(data);
        List<PermissionEntry> permissionEntries = new LinkedList<>(data.getPermissionEntries());
        PermissionEntry entry = new PermissionEntry(this.tryUUID(playerName), group.getName(), "");
        permissionEntries.removeIf(permissionEntry -> permissionEntry.getPermissionGroup().equalsIgnoreCase(group.getName()));

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
        permissionEntries.add(entry);
        data.setPermissionEntries(permissionEntries);
        this.playerCache.add(data);
    }


    public List<PermissionGroup> getPermissionGroups(String player) {
        List<PermissionGroup> permissionGroups = new LinkedList<>();
        CloudPlayerData data = this.getPlayerData(player);
        if (data != null) {
            for (PermissionEntry permissionEntry : data.getPermissionEntries()) {
                PermissionGroup permissionGroup = this.getPermissionGroupFromName(permissionEntry.getPermissionGroup());
                permissionGroups.add(permissionGroup);
            }
        }
        return permissionGroups;
    }

    public PermissionGroup getHighestPermissionGroup(String player) {
        List<PermissionGroup> list = this.getPermissionGroups(player);
        list.sort(Comparator.comparingInt(PermissionGroup::getId));
        return list.get(0) == null ? new DefaultPermissionGroup() : list.get(0);
    }

    public CloudPlayerData getPlayerData(String playerName) {
        for (CloudPlayerData cloudPlayerData : this.playerCache) {
            if (cloudPlayerData.getName().equalsIgnoreCase(playerName)) {
                return cloudPlayerData;
            }
        }
        return null;
    }
    public CloudPlayerData getPlayerData(UUID uuid) {
        for (CloudPlayerData cloudPlayerData : this.playerCache) {
            if (cloudPlayerData.getUuid().equals(uuid)) {
                return cloudPlayerData;
            }
        }
        return null;
    }

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
    
    public boolean hasPermission(String playerName, String permission) {
        boolean is = false;
        CloudPlayerData data = this.getPlayerData(playerName);
        if (data != null) {
            if (data.getPermissions().contains(permission)) {
                is = true;
            }
            for (PermissionEntry permissionEntry : data.getPermissionEntries()) {
                PermissionGroup permissionGroup = this.getPermissionGroupFromName(permissionEntry.getPermissionGroup());
                if (permissionGroup != null) {
                    for (String p : permissionGroup.getPermissions()) {
                        if (p.equalsIgnoreCase(permission)) {
                            is = true;
                            break;
                        }
                    }
                    for (String i : permissionGroup.getInheritances()) {
                        PermissionGroup inheritance = this.getPermissionGroupFromName(i);
                        if (inheritance != null) {
                            for (String p : inheritance.getPermissions()) {
                                if (p.equalsIgnoreCase(permission)) {
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

    public PermissionGroup getPermissionGroupFromName(String name) {
        for (PermissionGroup permissionGroup : this.permissionGroups) {
            if (permissionGroup.getName().equalsIgnoreCase(name)) {
                return permissionGroup;
            }
        }
        return null;
    }

    public Document save(File file, File directory, CloudDatabase database) {
        Document document = new Document(file);
        for (PermissionGroup permissionGroup : this.permissionGroups) {
            document.append(permissionGroup.getName(), permissionGroup);
        }
        document.save();

        for (CloudPlayerData cloudPlayerData : this.playerCache) {
            database.setPlayerData(cloudPlayerData.getUuid(), cloudPlayerData);
            //Document dataDoc = new Document();
            //dataDoc.appendAll(cloudPlayerData);
            //dataDoc.save(new File(directory, cloudPlayerData.getUuid() + ".json"));
        }
        this.clearInvalidUUIDs(directory);
        return document;
    }

    public void clearInvalidUUIDs(File directory) {
        new Thread(() -> {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                String uuid = file.getName().split("\\.")[0];
                try {
                    if (UUIDService.getName(UUID.fromString(uuid)) == null) {
                        file.delete();
                    }
                } catch (NullPointerException | IOException e) {
                    file.delete();
                }
            }
        }, "async_uuid_clear_cache").start();
    }

    public UUID getUUID(String name) {
        for (CloudPlayerData data : this.playerCache) {
            if (data.getName().equalsIgnoreCase(name)) {
                return data.getUuid();
            }
        }
        return null;
    }

    public UUID tryUUID(String name) {
        return this.getUUID(name) == null ? UUIDService.getUUID(name) : this.getUUID(name);
    }

    public String tryName(UUID uuid) {
        try {
            return this.getName(uuid) == null ? UUIDService.getName(uuid) : this.getName(uuid);
        } catch (IOException e) {
            return null;
        }
    }

    public String getName(UUID uuid) {
        for (CloudPlayerData data : this.playerCache) {
            if (data.getUuid().equals(uuid)) {
                return data.getName();
            }
        }
        return null;
    }


    public boolean isRegistered(String name) {
        return (this.getPlayerData(name) != null);
    }
}