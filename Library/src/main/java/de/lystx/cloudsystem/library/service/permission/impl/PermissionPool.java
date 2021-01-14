package de.lystx.cloudsystem.library.service.permission.impl;

import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInPermissionPool;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Getter
public class PermissionPool implements Serializable {

    private final List<PermissionGroup> permissionGroups;
    private final List<CloudPlayerData> playerCache;
    private final SimpleDateFormat format;

    public PermissionPool() {
        this.permissionGroups = new LinkedList<>();
        this.playerCache = new LinkedList<>();
        this.format = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss", Locale.GERMAN);
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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss");
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
        CloudPlayerData data = this.getPlayerData(playerName);
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

    public CloudPlayerData getPlayerDataOrDefault(String playerName) {
        for (CloudPlayerData cloudPlayerData : this.playerCache) {
            if (cloudPlayerData.getName().equalsIgnoreCase(playerName)) {
                return cloudPlayerData;
            }
        }
        CloudPlayerData data = new CloudPlayerData(UUID.randomUUID(), playerName, "Player", "Player", "", new LinkedList<>(), "127.0.0.1", true);
        data.setDefault(true);
        return data;
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
        return document;
    }


    public UUID getUUID(String name) {
        for (CloudPlayerData data : this.playerCache) {
            if (data.getName().equalsIgnoreCase(name)) {
                return data.getUuid();
            }
        }
        return null;
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
