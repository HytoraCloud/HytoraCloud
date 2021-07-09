package de.lystx.hytoracloud.driver.service.permission.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerPermissionGroupAddCloudEvent;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerPermissionGroupRemoveCloudEvent;
import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketUpdatePermissionPool;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.service.database.IDatabase;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.permission.PermissionService;
import de.lystx.hytoracloud.driver.service.player.IPermissionUser;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerInformation;

import de.lystx.hytoracloud.driver.service.uuid.UUIDService;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Getter @Setter
public class PermissionPool implements Serializable {

    /**
     * All cached {@link PermissionGroup}s
     */
    private List<PermissionGroup> cachedPermissionGroups;

    /**
     * All cached (from {@link IDatabase}) {@link PlayerInformation}s
     */
    private List<PlayerInformation> cachedCloudPlayers;

    /**
     * If the pool is enabled (perms.json)
     */
    private boolean enabled;

    /**
     * Creates a new Pool with empty values
     * and enabled pool
     */
    public PermissionPool() {
        this.enabled = true;
        this.cachedPermissionGroups = new LinkedList<>();
        this.cachedCloudPlayers = new LinkedList<>();
    }

    /*
    ======================================
      PermissionGroup managing
    ======================================
     */


    /**
     * Loads the permissionPool by using the perms.json
     * and going back many folders
     * > Not recommended though
     */
    public PermissionPool loadNonePacketPool() {
        this.cachedCloudPlayers.clear();
        this.cachedPermissionGroups.clear();

        File permsFile = new File("../../../../../perms.json");
        File playerDirectory = new File("../../../../../database/players/");

        this.loadGroupsFromFile(permsFile);

        for (File file : Objects.requireNonNull(playerDirectory.listFiles())) {
            JsonEntity jsonEntity = new JsonEntity(file);
            this.cachedCloudPlayers.add(jsonEntity.getAs(PlayerInformation.class));
        }
        return this;
    }

    /**
     * Checks for lowest ID if none is found returns DefaultPermissionGroup.class
     * @return default permissionGroup
     */
    public PermissionGroup getLowestPermissionGroupOrDefault() {
        if (this.cachedPermissionGroups != null) {
            this.cachedPermissionGroups = new LinkedList<>(this.cachedPermissionGroups);
            this.cachedPermissionGroups.sort(Comparator.comparingInt(PermissionGroup::getId));
            try {
                if (!this.cachedPermissionGroups.isEmpty()) {
                    return this.cachedPermissionGroups.get(this.cachedPermissionGroups.size() - 1);
                }
            } catch (Exception e) {
                //Ignoring
            }
        }
        return new PermissionGroup("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new HashMap<>());
    }

    /**
     * Checks if rank of player is valid
     *
     * @param uniqueId the uuid of the player
     * @param permissionGroup the group to check
     * @return valid
     */
    public boolean isRankValid(UUID uniqueId, PermissionGroup permissionGroup) {
        PlayerInformation data = this.getPlayerInformation(uniqueId);
        if (data == null) {
            return false;
        }
        PermissionEntry entry = data.getPermissionEntryOfGroup(permissionGroup.getName());
        if (entry == null) {
            return false;
        }
        String entryValidTime = entry.getValidTime();

        if (entryValidTime.equalsIgnoreCase("lifetime") || entryValidTime.trim().isEmpty()) {
            return true;
        }
        try {
            Date today = new Date();
            Date endDate = this.getFormat().parse(entryValidTime);
            long diffInMillies = endDate.getTime() - today.getTime();
            long d =  TimeUnit.MILLISECONDS.convert(diffInMillies,TimeUnit.MILLISECONDS);
            return (d >= 1);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Removes a group from a player
     *
     * @param uniqueId uuid of the player
     * @param group group to remove
     */
    public void removePermissionGroupFromUser(UUID uniqueId, PermissionGroup group) {
        PlayerInformation offlinePlayer = this.getPlayerInformationOrDefault(uniqueId);
        if (offlinePlayer == null) {
            return;
        }

        List<PermissionEntry> permissionEntries = offlinePlayer.getPermissionEntries(); //The entries
        permissionEntries.removeIf(entry -> entry.getPermissionGroup().equalsIgnoreCase(group.getName())); //Removing the group
        offlinePlayer.setPermissionEntries(permissionEntries); //Updating the entries

        //Updating player and calling event
        this.updatePlayer(offlinePlayer);
        CloudDriver.getInstance().callEvent(new CloudPlayerPermissionGroupRemoveCloudEvent(this.getNameByUUID(uniqueId), group));
    }

    /**
     * Gives a player a permissionGroup
     * @param uniqueId the uuid of the player
     * @param group the group to add
     * @param time the time
     * @param validity the unit
     */
    @SneakyThrows
    public void addPermissionGroupToUser(UUID uniqueId, PermissionGroup group, Integer time, PermissionValidity validity) {

        PlayerInformation playerInformation = this.getPlayerInformationOrDefault(uniqueId);
        List<PermissionEntry> permissionEntries = new LinkedList<>(playerInformation.getPermissionEntries()); //Safely defining list value
        PermissionEntry entry = new PermissionEntry(group.getName(), "");
        permissionEntries.removeIf(permissionEntry -> permissionEntry.getPermissionGroup().equalsIgnoreCase(group.getName()));//Removing old group

        permissionEntries.add(entry); //add group to entries

        if (time > 0) {
            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(getFormat().parse(this.getFormat().format(new Date())));
            calendarInstance.add(validity.toCalendar(), time);
            entry.setValidTime(this.getFormat().format(calendarInstance.getTime()));
        }
        playerInformation.setPermissionEntries(permissionEntries); //Set entries again

        //Adding data back to cache and calling event
        this.updatePlayer(playerInformation);
        CloudDriver.getInstance().callEvent(new CloudPlayerPermissionGroupAddCloudEvent(getNameByUUID(uniqueId), group, time, validity));
    }

    /**
     * Returns all cached groups of player
     *
     * @param uniqueId the uuid of the player
     * @return list of groups
     */
    @SneakyThrows
    public List<PermissionGroup> getCachedPermissionGroups(UUID uniqueId) {

        PlayerInformation data = this.getPlayerInformation(uniqueId);

        if (data == null) { //Player has no rank yet returning lowest rank or default rank
            data = this.getDefaultPlayerInformation(uniqueId, getNameByUUID(uniqueId), "-1"); //Setting ip to default
        }

        return new LinkedList<>(data.getPermissionGroups());
    }

    /**
     * Returns highest Group of player
     *
     * @param uniqueId the uuid of the player
     * @return group sorted by ID
     */
    public PermissionGroup getHighestPermissionGroup(UUID uniqueId) {
        try {
            List<PermissionGroup> list = this.getCachedPermissionGroups(uniqueId);
            list.sort(Comparator.comparingInt(PermissionGroup::getId)); //Sorting by id

            return list.isEmpty() ? this.getLowestPermissionGroupOrDefault() : list.get(0); //Getting with the lowest id or the highest
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Streams through groups
     *
     * @param name the name of the group
     * @return group from Name
     */
    public PermissionGroup getPermissionGroupByName(String name) {
        return this.cachedPermissionGroups.stream().filter(permissionGroup -> permissionGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Checks if a player has a certain permission
     *
     * @param uniqueId the uuid of the player
     * @param permission the permission
     * @return true if player has the permission
     */
    public boolean hasPermission(UUID uniqueId, String permission) {
        if (!this.enabled) {
            return false;
        }

        boolean hasPermission = false;
        PlayerInformation offlinePlayer = this.getPlayerInformation(uniqueId);  //Gets offline data

        if (offlinePlayer != null) {
            //Not null go on checking
            if (offlinePlayer.getExclusivePermissions().contains(permission) || offlinePlayer.getExclusivePermissions().contains("*")) {
                hasPermission = true;
            }

            //Safely iterating through permissionGroups
            for (PermissionGroup permissionGroup : new LinkedList<>(offlinePlayer.getAllPermissionGroups())) {
                //Checking if group has all rights (*) or the specific permission
                if (permissionGroup.getPermissions().contains("*") || permissionGroup.getPermissions().contains(permission)) {
                    hasPermission = true;
                    break;
                }

                //Checking if inheritances have the perms
                for (PermissionGroup inheritance : permissionGroup.getInheritances()) {
                    if (inheritance.getPermissions().contains("*") || inheritance.getPermissions().contains(permission)) {
                        hasPermission = true;
                        break;
                    }
                }
            }
        }

        //Returning value
        return hasPermission;
    }

    /*
    ======================================
      UUID Fetching with cache
    ======================================
     */

    /**
     * Tries to get UUID by name
     * If its not cached from pool it gets from web
     *
     * @param name the name of the player
     * @return uuid from cache or web
     */
    public UUID getUUIDByName(String name) {
        UUID uniqueId = null;

        PlayerInformation playerInformation = this.cachedCloudPlayers.stream().filter(offlineCloudPlayer1 -> offlineCloudPlayer1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (playerInformation != null) {
            uniqueId = playerInformation.getUniqueId();
        }

        return uniqueId == null ? UUIDService.getInstance().getUUID(name) : uniqueId;
    }

    /**
     * Tries to get UUID from IP by iterating through the cache and checking
     * if the cache contains the UUID (Data) and the returns it
     *
     * @param socketAddress the address youre trying to get the uuid from
     * @return uuid or null (if not found)
     */
    public UUID getUniqueIdFromIpAddress(InetSocketAddress socketAddress) {
        PlayerInformation offlinePlayer = this.cachedCloudPlayers.stream().filter(cloudPlayerData -> cloudPlayerData.getIpAddress().equalsIgnoreCase(socketAddress.getAddress().getHostAddress())).findFirst().orElse(null);
        return offlinePlayer == null ? null : offlinePlayer.getUniqueId();
    }

    /**
     * Tries to get Name by UUID
     * If its not cached from pool it gets from web
     *
     * @param uuid the uuid of the player
     * @return name of null (if not found)
     */
    public String getNameByUUID(UUID uuid) {
        String name = null;
        PlayerInformation playerInformation = this.cachedCloudPlayers.stream().filter(offlineCloudPlayer1 -> offlineCloudPlayer1.getUniqueId() == uuid).findFirst().orElse(null);
        if (playerInformation != null) {
            name = playerInformation.getName();
        }
        return name == null ? UUIDService.getInstance().getName(uuid) : name;
    }

    /*
    ======================================
             Other methods
    ======================================
     */

    /**
     * Iterates through all the permissions of a player
     *
     * @param uniqueId > UUID of the player
     * @param ipAddress > IP of the player to set default Data if player not exists
     * @param accept > Consumer<String> that accepts all the permissions
     */
    public void updatePermissions(UUID uniqueId, String ipAddress, Consumer<String> accept) {
        if (!this.isAvailable() ) {
            System.out.println("[CloudAPI] Couldn't update Permissions for " + uniqueId + " because PermissionPool is not available!");
            return;
        }
        PlayerInformation offlinePlayer = this.getPlayerInformation(uniqueId);
        if (offlinePlayer != null) {
            if (offlinePlayer.getPermissionEntries().isEmpty()) {
                offlinePlayer.getPermissionEntries().add(new PermissionEntry(this.getLowestPermissionGroupOrDefault().getName(), ""));
                this.updatePlayer(offlinePlayer);
                this.update();
            }
        } else {
            this.cachedCloudPlayers.add(this.getDefaultPlayerInformation(uniqueId, getNameByUUID(uniqueId), "-1"));
        }

        PlayerInformation orDefault = this.getPlayerInformationOrDefault(uniqueId); //Getting player or default
        boolean changedSomething = false;

        //Safely iterating through all groups
        for (PermissionGroup permissionGroup : new LinkedList<>(orDefault.getAllPermissionGroups())) {
            if (!this.isRankValid(uniqueId, permissionGroup)) {
                changedSomething = true;
                this.removePermissionGroupFromUser(uniqueId, permissionGroup);
                orDefault.getPermissionEntries().remove(orDefault.getPermissionEntryOfGroup(permissionGroup.getName()));
            }
        }

        //If its default that means the uuid and ip are not set so we will do this here
        if (orDefault.isDefault()) {
            orDefault.setUniqueId(uniqueId);
            orDefault.setIpAddress(ipAddress);
        }

        //If a rank has been removed (something changed) we have to update to make all changes sync over the network
        if (changedSomething) {
            this.updatePlayer(orDefault); //Updating the player
            this.update(); //Updating the whole pool
        }

        List<String> permissions = new LinkedList<>();

        //All inheritances
        for (PermissionGroup group : orDefault.getAllPermissionGroups()) {
            permissions.addAll(group.getPermissions());
            group.getInheritances().forEach(i -> permissions.addAll(i.getPermissions()));
        }

        //Adding all permissions that he has exclusively
        permissions.addAll(orDefault.getExclusivePermissions());
        permissions.forEach(accept); //Accepting the consumer for all permissions
    }

    /**
     * Checks if player is registered in Database
     *
     * @param uniqueId the uuid of the player
     * @return if player is registered
     */
    public boolean isRegistered(UUID uniqueId) {
        return (this.getPlayerInformation(uniqueId) != null);
    }

    /**
     * Returns the {@link SimpleDateFormat} for this pool
     *
     * @return dateFormat
     */
    public SimpleDateFormat getFormat() {
        return new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss", Locale.GERMAN);
    }

    /**
     * Checks if pool is available
     * @return boolean
     */
    public boolean isAvailable() {
        return (!this.cachedPermissionGroups.isEmpty() || !this.cachedCloudPlayers.isEmpty());
    }

    /**
     * Updates the permissionPool
     */
    public void update() {
        CloudDriver.getInstance().setPermissionPool(this);
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            PermissionService instance = CloudDriver.getInstance().getInstance(PermissionService.class);
            if (instance != null) {
                instance.setIgnore(true);
                instance.save(instance.getFile(), CloudDriver.getInstance().getInstance(FileService.class).getCloudPlayerDirectory(), CloudDriver.getInstance().getDatabaseManager().getDatabase());
                instance.reload();
            }
        }
        if (CloudDriver.getInstance().getConnection() == null) {
            return;
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketUpdatePermissionPool(this));
    }

    /**
     * Loads {@link PermissionGroup}s from the perms.json (or any other file)
     *
     * @param file the file to load it from
     */
    public void loadGroupsFromFile(File file) {
        this.cachedPermissionGroups.clear();
        JsonEntity jsonEntity = new JsonEntity(file);
        for (String key : jsonEntity.keys()) {
            if (!key.equalsIgnoreCase("enabled")) {
                PermissionGroup permissionGroup = jsonEntity.getObject(key, PermissionGroup.class);
                this.cachedPermissionGroups.add(permissionGroup);
            } else {
                this.enabled = jsonEntity.getBoolean(key);
            }
        }
    }

    /*
    ======================================
      (Offline-)Player managing
    ======================================
     */

    /**
     * Adds or removes a permission from a player
     *
     * @param uniqueId the uuid of the player
     * @param permission the permission
     * @param add if it should be added or removed
     */
    public void setPermissionToUser(UUID uniqueId, String permission, boolean add) {
        PlayerInformation data = this.getPlayerInformation(uniqueId);
        if (data == null) {
            return;
        }
        this.cachedCloudPlayers.remove(data);
        List<String> permissions = data.getExclusivePermissions();
        if (add) {
            permissions.add(permission);
        } else {
            permissions.remove(permission);
        }
        data.setPermissions(permissions);
        this.cachedCloudPlayers.add(data);
    }

    /**
     * Adds a permission to a user
     *
     * @param uniqueId the uuid of the user
     * @param permission the permission
     */
    public void addPermissionToUser(UUID uniqueId, String permission) {
        this.setPermissionToUser(uniqueId, permission, true);
    }

    /**
     * Removes a permission from a user
     *
     * @param uniqueId the uuid of the user
     * @param permission the permission
     */
    public void removePermissionFromUser(UUID uniqueId, String permission) {
        this.setPermissionToUser(uniqueId, permission, false);
    }

    /**
     * Updates a Player Value
     *
     * @param offlinePlayer the player
     */
    public void updatePlayer(PlayerInformation offlinePlayer) {
        if (offlinePlayer == null) {
            return;
        }
        PlayerInformation oldPlayerInformation = this.getPlayerInformation(offlinePlayer.getUniqueId());
        if (oldPlayerInformation == null) {
            this.cachedCloudPlayers.add(offlinePlayer);
            return;
        }
        try {
            this.cachedCloudPlayers.set(this.cachedCloudPlayers.indexOf(oldPlayerInformation), offlinePlayer);
        } catch (IndexOutOfBoundsException e) {
            this.cachedCloudPlayers.remove(oldPlayerInformation);
            this.cachedCloudPlayers.add(offlinePlayer);
        }
    }

    /**
     * Creates a default {@link PlayerInformation}
     *
     * @param uuid the uuid of the player
     * @param name the name
     * @param ip the ip
     * @return created player
     */
    public PlayerInformation getDefaultPlayerInformation(UUID uuid, String name, String ip) {
        PlayerInformation playerInformation = new PlayerInformation(uuid, name, Collections.singletonList(new PermissionEntry(this.getLowestPermissionGroupOrDefault().getName(), "")), new LinkedList<>(), ip, true, new Date().getTime(), 0L);
        playerInformation.setDefault(true);
        return playerInformation;
    }

    /**
     * Gets the {@link PlayerInformation} by name
     *
     * @param playerName the name
     * @return offlinePlayer
     */
    public PlayerInformation getPlayerInformation(String playerName) {
        return this.cachedCloudPlayers.stream()
                .filter(offlineCloudPlayer ->
                        offlineCloudPlayer.getName().equalsIgnoreCase(playerName)
                ).findFirst()
                .orElse(this.getDefaultPlayerInformation(this.getUUIDByName(playerName), playerName, "-1"));
    }

    /**
     * Gets the {@link PlayerInformation} by name
     *
     * @param uniqueId the uuid of the player
     * @return offlinePlayer
     */
    public PlayerInformation getPlayerInformation(UUID uniqueId) {
        if (uniqueId == null) {
            return null;
        }

        return this.cachedCloudPlayers.stream().filter(cp -> cp.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    /**
     * Gets a default {@link PlayerInformation}
     *
     * @param uniqueId the uuid of the player
     * @return its data or the default
     */
    public PlayerInformation getPlayerInformationOrDefault(UUID uniqueId) {
        PlayerInformation pre = this.getPlayerInformation(uniqueId);
        return pre == null ? this.getDefaultPlayerInformation(uniqueId, getNameByUUID(uniqueId), "-1") : pre;
    }

    /**
     * Loads a {@link IPermissionUser} from uuid
     *
     * @param uniqueId the uuid of the player
     * @return permissionPlayer
     */
    public IPermissionUser getPermissionPlayer(UUID uniqueId) {
        return this.getPlayerInformation(uniqueId);
    }

    /**
     * Loads a {@link IPermissionUser} from name
     *
     * @param name the name of the player
     * @return permissionPlayer
     */
    public IPermissionUser getPermissionPlayer(String name) {
        return this.getPlayerInformation(name);
    }

    /**
     * Modifies a user async and threadSafe
     *
     * @param uniqueId the uuid of the player
     * @param consumer the consumer to work with
     */
    public void modifyPlayer(UUID uniqueId, Consumer<IPermissionUser> consumer) {
        CloudDriver.getInstance().execute(() -> {
            IPermissionUser IPermissionPlayer = getPermissionPlayer(uniqueId);
            consumer.accept(IPermissionPlayer);
        });
    }

    /**
     * Modifies a user async and threadSafe
     *
     * @param name the name of the player
     * @param consumer the consumer to work with
     */
    public void modifyPlayer(String name, Consumer<IPermissionUser> consumer) {
        CloudDriver.getInstance().execute(() -> {
            IPermissionUser IPermissionPlayer = getPermissionPlayer(name);
            consumer.accept(IPermissionPlayer);
        });
    }


}