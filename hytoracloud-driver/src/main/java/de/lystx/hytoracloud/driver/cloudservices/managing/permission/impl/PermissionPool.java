package de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.ObjectPool;
import de.lystx.hytoracloud.driver.commons.events.player.permissions.DriverEventPlayerGroupReceive;
import de.lystx.hytoracloud.driver.commons.events.player.permissions.DriverEventPlayerGroupRemove;
import de.lystx.hytoracloud.driver.commons.interfaces.ScheduledForVersion;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketUpdatePermissionPool;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.IDatabase;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.PermissionService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.IPermissionUser;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;

import de.lystx.hytoracloud.driver.commons.requests.base.DriverQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Getter @Setter
public class PermissionPool implements Serializable, ObjectPool<OfflinePlayer> {

    private static final long serialVersionUID = -501568977137292070L;

    /**
     * All cached {@link PermissionGroup}s
     */
    private List<PermissionGroup> permissionGroups;

    /**
     * All cached (from {@link IDatabase}) {@link OfflinePlayer}s
     */
    private List<OfflinePlayer> cachedObjects;

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
        this.permissionGroups = new LinkedList<>();
        this.cachedObjects = new LinkedList<>();
    }

    /*
    ======================================
      PermissionGroup managing
    ======================================
     */


    /**
     * Checks for lowest ID if none is found returns DefaultPermissionGroup.class
     * @return default permissionGroup
     */
    public PermissionGroup getDefaultPermissionGroup() {
        if (this.permissionGroups != null) {
            this.permissionGroups = new LinkedList<>(this.permissionGroups);
            this.permissionGroups.sort(Comparator.comparingInt(PermissionGroup::getId));
            try {
                if (!this.permissionGroups.isEmpty()) {
                    return this.permissionGroups.get(this.permissionGroups.size() - 1);
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
        OfflinePlayer data = this.getCachedObject(uniqueId);
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
        OfflinePlayer offlinePlayer = this.getCachedObject(uniqueId);
        if (offlinePlayer == null) {OfflinePlayer pre = this.getCachedObject(uniqueId);
            offlinePlayer = new OfflinePlayer(uniqueId, this.getNameByUUID(uniqueId), Collections.singletonList(new PermissionEntry(CloudDriver.getInstance().getPermissionPool().getDefaultPermissionGroup().getName(), "")), new LinkedList<>(), "0", true, true, new Date().getTime(), 0L, new HashMap<>());
        }

        List<PermissionEntry> permissionEntries = offlinePlayer.getPermissionEntries(); //The entries
        permissionEntries.removeIf(entry -> entry.getPermissionGroup().equalsIgnoreCase(group.getName())); //Removing the group
        offlinePlayer.setPermissionEntries(permissionEntries); //Updating the entries

        //Updating player and calling event
        this.update(offlinePlayer);
        CloudDriver.getInstance().callEvent(new DriverEventPlayerGroupRemove(this.getNameByUUID(uniqueId), group));
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

        OfflinePlayer offlinePlayer = this.getCachedObject(uniqueId);

        if (offlinePlayer == null) {
            offlinePlayer = new OfflinePlayer(uniqueId, this.getNameByUUID(uniqueId), Collections.singletonList(new PermissionEntry(CloudDriver.getInstance().getPermissionPool().getDefaultPermissionGroup().getName(), "")), new LinkedList<>(), "0", true, true, new Date().getTime(), 0L, new HashMap<>());
        }


        List<PermissionEntry> permissionEntries = new LinkedList<>(offlinePlayer.getPermissionEntries()); //Safely defining list value
        PermissionEntry entry = new PermissionEntry(group.getName(), "");
        permissionEntries.removeIf(permissionEntry -> permissionEntry.getPermissionGroup().equalsIgnoreCase(group.getName()));//Removing old group

        permissionEntries.add(entry); //add group to entries

        if (time > 0) {
            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(getFormat().parse(this.getFormat().format(new Date())));
            calendarInstance.add(validity.toCalendar(), time);
            entry.setValidTime(this.getFormat().format(calendarInstance.getTime()));
        }
        offlinePlayer.setPermissionEntries(permissionEntries); //Set entries again

        //Adding data back to cache and calling event
        this.update(offlinePlayer);
        CloudDriver.getInstance().callEvent(new DriverEventPlayerGroupReceive(getNameByUUID(uniqueId), group, time, validity));
    }

    /**
     * Returns all cached groups of player
     *
     * @param uniqueId the uuid of the player
     * @return list of groups
     */
    @SneakyThrows
    public List<PermissionGroup> getPermissionGroups(UUID uniqueId) {

        OfflinePlayer data = this.getCachedObject(uniqueId);

        if (data == null) { //Player has no rank yet returning lowest rank or default rank
            data = new OfflinePlayer(uniqueId, getNameByUUID(uniqueId), Collections.singletonList(new PermissionEntry(CloudDriver.getInstance().getPermissionPool().getDefaultPermissionGroup().getName(), "")), new LinkedList<>(), "0", true, true, new Date().getTime(), 0L, new HashMap<>());
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
            List<PermissionGroup> list = this.getPermissionGroups(uniqueId);
            list.sort(Comparator.comparingInt(PermissionGroup::getId)); //Sorting by id

            return list.isEmpty() ? this.getDefaultPermissionGroup() : list.get(0); //Getting with the lowest id or the highest
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
        return this.permissionGroups.stream().filter(permissionGroup -> permissionGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
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
        OfflinePlayer offlinePlayer = this.getCachedObject(uniqueId);  //Gets offline data

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

        OfflinePlayer offlinePlayer = this.cachedObjects.stream().filter(offlineCloudPlayer1 -> offlineCloudPlayer1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (offlinePlayer != null) {
            uniqueId = offlinePlayer.getUniqueId();
        }

        return uniqueId == null ? CloudDriver.getInstance().getMojangPool().getUniqueId(name) : uniqueId;
    }

    /**
     * Tries to get UUID from IP by iterating through the cache and checking
     * if the cache contains the UUID (Data) and the returns it
     *
     * @param socketAddress the address youre trying to get the uuid from
     * @return uuid or null (if not found)
     */
    public UUID getUniqueIdFromIpAddress(InetSocketAddress socketAddress) {
        OfflinePlayer offlinePlayer = this.cachedObjects.stream().filter(cloudPlayerData -> cloudPlayerData.getIpAddress().equalsIgnoreCase(socketAddress.getAddress().getHostAddress())).findFirst().orElse(null);
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
        OfflinePlayer offlinePlayer = this.cachedObjects.stream().filter(offlineCloudPlayer1 -> offlineCloudPlayer1.getUniqueId() == uuid).findFirst().orElse(null);
        if (offlinePlayer != null) {
            name = offlinePlayer.getName();
        }
        return name == null ? CloudDriver.getInstance().getMojangPool().getName(uuid) : name;
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
            System.out.println("[CloudDriver] Couldn't update Permissions for " + uniqueId + " because PermissionPool is not available!");
            return;
        }
        OfflinePlayer offlinePlayer = this.getCachedObject(uniqueId);
        if (offlinePlayer != null) {
            if (offlinePlayer.getPermissionEntries().isEmpty()) {
                offlinePlayer.getPermissionEntries().add(new PermissionEntry(this.getDefaultPermissionGroup().getName(), ""));
                this.update(offlinePlayer);
                this.update();
            }
        } else {
            this.cachedObjects.add(new OfflinePlayer(uniqueId, this.getNameByUUID(uniqueId), Collections.singletonList(new PermissionEntry(CloudDriver.getInstance().getPermissionPool().getDefaultPermissionGroup().getName(), "")), new LinkedList<>(), "0", true, true, new Date().getTime(), 0L, new HashMap<>()));
        }

        OfflinePlayer orDefault = this.getCachedObject(uniqueId); //Getting player or default


        if (orDefault == null) {
            orDefault = new OfflinePlayer(uniqueId, this.getNameByUUID(uniqueId), Collections.singletonList(new PermissionEntry(CloudDriver.getInstance().getPermissionPool().getDefaultPermissionGroup().getName(), "")), new LinkedList<>(), "0", true, true, new Date().getTime(), 0L, new HashMap<>());
        }

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
            this.update(orDefault); //Updating the player
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
        return (this.getCachedObject(uniqueId) != null);
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
        return (!this.permissionGroups.isEmpty() || !this.cachedObjects.isEmpty());
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
        OfflinePlayer offlinePlayer = this.getCachedObject(uniqueId);
        if (offlinePlayer == null) {
            return;
        }
        List<String> permissions = offlinePlayer.getExclusivePermissions();
        if (add) {
            permissions.add(permission);
        } else {
            permissions.remove(permission);
        }
        offlinePlayer.setPermissions(permissions);
        this.update(offlinePlayer);
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
    public void update(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null) {
            return;
        }
        OfflinePlayer oldOfflinePlayer = this.getCachedObject(offlinePlayer.getUniqueId());
        if (oldOfflinePlayer == null) {
            this.cachedObjects.add(offlinePlayer);
            return;
        }
        try {
            this.cachedObjects.set(this.cachedObjects.indexOf(oldOfflinePlayer), offlinePlayer);
        } catch (IndexOutOfBoundsException e) {
            //Ignoring exception
        }
    }

    /**
     * Gets the {@link OfflinePlayer} by name
     *
     * @param name the name
     * @return offlinePlayer
     */
    public OfflinePlayer getCachedObject(String name) {
        if (name == null) {
            return null;
        }
        return this.cachedObjects.stream().filter(cp -> cp.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Gets the {@link OfflinePlayer} by name
     *
     * @param uniqueId the uuid of the player
     * @return offlinePlayer
     */
    public OfflinePlayer getCachedObject(UUID uniqueId) {
        if (uniqueId == null) {
            return null;
        }
        return this.cachedObjects.stream().filter(cp -> cp.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override @ScheduledForVersion("STABLE-1.9")
    public void getObjectAsync(String name, Consumer<OfflinePlayer> consumer) {

    }

    @Override @ScheduledForVersion("STABLE-1.9")
    public void getObjectAsync(UUID uniqueId, Consumer<OfflinePlayer> consumer) {

    }

    @Override @ScheduledForVersion("STABLE-1.9")
    public DriverQuery<OfflinePlayer> getObjectSync(String name) {
        return null;
    }

    @Override @ScheduledForVersion("STABLE-1.9")
    public DriverQuery<OfflinePlayer> getObjectSync(UUID uniqueId) {
        return null;
    }

    /**
     * Loads a {@link IPermissionUser} from uuid
     *
     * @param uniqueId the uuid of the player
     * @return permissionPlayer
     */
    public IPermissionUser getPermissionPlayer(UUID uniqueId) {
        return this.getCachedObject(uniqueId);
    }

    /**
     * Loads a {@link IPermissionUser} from name
     *
     * @param name the name of the player
     * @return permissionPlayer
     */
    public IPermissionUser getPermissionPlayer(String name) {
        return this.getCachedObject(name);
    }

    /**
     * Modifies a user async and threadSafe
     *
     * @param uniqueId the uuid of the player
     * @param consumer the consumer to work with
     */
    public void modifyPlayer(UUID uniqueId, Consumer<IPermissionUser> consumer) {
        CloudDriver.getInstance().getExecutorService().execute(() -> {
            IPermissionUser permissionPlayer = getPermissionPlayer(uniqueId);
            consumer.accept(permissionPlayer);
        });
    }

    /**
     * Modifies a user async and threadSafe
     *
     * @param name the name of the player
     * @param consumer the consumer to work with
     */
    public void modifyPlayer(String name, Consumer<IPermissionUser> consumer) {
        CloudDriver.getInstance().getExecutorService().execute(() -> {
            IPermissionUser permissionPlayer = getPermissionPlayer(name);
            consumer.accept(permissionPlayer);
        });
    }


    @NotNull
    @Override
    public Iterator<OfflinePlayer> iterator() {
        return this.getCachedObjects().iterator();
    }
}