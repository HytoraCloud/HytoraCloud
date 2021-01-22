package de.lystx.cloudsystem.library.service.permission.impl;

import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInPermissionPool;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.util.UUIDService;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        List<String> permissions = data.getPermissions();
        if (add) {
            permissions.add(permission);
        } else {
            permissions.remove(permission);
        }
        this.playerCache.remove(data);
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

    public Boolean isRankValid(String playerName) {
        CloudPlayerData data = this.getPlayerData(playerName);
        if (data == null) {
            return false;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(this.format.toPattern());
        String bis = data.getValidadilityTime();
        if (bis.equalsIgnoreCase("lifetime") || bis.trim().isEmpty()) {
            return true;
        }
        LocalDate date1 = LocalDate.parse(this.format.format(new Date()), dtf);
        LocalDate date2 = LocalDate.parse(bis, dtf);
        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        return (daysBetween >= 1);
    }

    public void updatePermissionGroup(String playerName, PermissionGroup group, Integer days) {
        CloudPlayerData data = this.getPlayerDataOrDefault(playerName);
        if (data == null) {
            return;
        }
        this.playerCache.remove(data);
        data.setPermissionGroup(group.getName());
        if (days <= 0) {
            data.setTempPermissionGroup(group.getName());
            data.setValidadilityTime("");
        } else {
            if (this.getPermissionGroup(playerName) != null)  {
                data.setPermissionGroup(this.getPermissionGroup(playerName).getName());
            } else {
                data.setPermissionGroup(group.getName());
            }

            Calendar c = Calendar.getInstance();
            try{
                c.setTime(format.parse(this.format.format(new Date())));
            } catch(ParseException ignored){}
            c.add(Calendar.DAY_OF_MONTH, days);
            data.setValidadilityTime(this.format.format(c.getTime()));
        }
        this.playerCache.add(data);
    }


    public PermissionGroup getPermissionGroup(String player) {
        PermissionGroup group = null;
        for (CloudPlayerData cloudPlayerData : this.playerCache) {
            if (cloudPlayerData.getName().equalsIgnoreCase(player)) {
                group = this.getPermissionGroupFromName(cloudPlayerData.getTempPermissionGroup());
            }
        }
        return group == null ? new DefaultPermissionGroup() : group;
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
            CloudPlayerData data = new CloudPlayerData(Objects.requireNonNull(UUIDService.getUUID(playerName)), playerName, "Player", "Player", "", new LinkedList<>(), "127.0.0.1", true, new Date().getTime(), new Date().getTime());
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
            PermissionGroup permissionGroup = this.getPermissionGroupFromName(data.getPermissionGroup());
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

    public Document save(File file, File directory) {
        Document document = new Document(file);
        for (PermissionGroup permissionGroup : this.permissionGroups) {
            document.append(permissionGroup.getName(), permissionGroup);
        }
        document.save();

        for (CloudPlayerData cloudPlayerData : this.playerCache) {
            Document dataDoc = new Document();
            dataDoc.appendAll(cloudPlayerData);
            dataDoc.save(new File(directory, cloudPlayerData.getUuid() + ".json"));
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