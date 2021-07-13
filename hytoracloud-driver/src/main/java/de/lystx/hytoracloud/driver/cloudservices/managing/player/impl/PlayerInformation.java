package de.lystx.hytoracloud.driver.cloudservices.managing.player.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionEntry;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.IPermissionUser;



import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

@Getter @Setter @AllArgsConstructor
public class PlayerInformation implements Serializable, IPermissionUser, Objectable<PlayerInformation> {

    /**
     * The uuid of this player
     */
    private UUID uniqueId;

    /**
     * The name of this player
     */
    private String name;

    /**
     * The loaded permission entries
     */
    private List<PermissionEntry> permissionEntries;

    /**
     * Exclusive permissions
     */
    private List<String> permissions;

    /**
     * The ip address
     */
    private String ipAddress;

    /**
     * if server messages received
     */
    private boolean notifyServerStart;

    /**
     * If this is a default created
     */
    private boolean isDefault;

    /**
     * First login
     */
    private long firstLogin;

    /**
     * last login
     */
    private long lastLogin;

    /**
     * Properties
     */
    private Map<String, PropertyObject> properties;

    public PlayerInformation(UUID uniqueId, String name, List<PermissionEntry> permissionEntries, List<String> permissions, String ipAddress, boolean notifyServerStart, long firstLogin, long lastLogin) {
        this(uniqueId, name, permissionEntries, permissions, ipAddress, notifyServerStart, false, firstLogin, lastLogin, new HashMap<>());
    }

    /**
     * Adds a property to this data
     * @param name the name (e.g "global")
     * @param data the data (e.g. coins or something)
     */
    public void addProperty(String name, PropertyObject data) {
        this.properties.put(name, data);
    }

    /**
     * Searches for a property with a given name
     *
     * @param name the name of the property
     * @return jsonObject
     */
    public PropertyObject getProperty(String name) {
        return this.properties.get(name);
    }

    /**
     * Gets Entry
     * @param group the group
     * @return PermissionEntry from group (e.g. "Admin")
     */
    public PermissionEntry getPermissionEntryOfGroup(String group) {
        return this.permissionEntries.stream().filter(permissionEntry -> group.equalsIgnoreCase(permissionEntry.getPermissionGroup())).findFirst().orElse(null);
    }

    /**
     * Returns all the {@link PermissionGroup}s
     * @return list of groups
     */
    public List<PermissionGroup> getPermissionGroups() {
        List<PermissionGroup> list = new ArrayList<>();
        for (PermissionEntry permissionEntry : this.permissionEntries) {
            PermissionGroup permissionGroup = CloudDriver.getInstance().getPermissionPool().getPermissionGroupByName(permissionEntry.getPermissionGroup());
            if (permissionGroup != null) {
                list.add(permissionGroup);
            }
        }
        return list;
    }

    /**
     * Returns highest Group of player
     * @return (group sorted by IDS)
     */
    public PermissionGroup getHighestPermissionGroup() {
        try {
            List<PermissionGroup> list = this.getPermissionGroups();
            list.sort(Comparator.comparingInt(PermissionGroup::getId));
            return list.isEmpty() ? null : list.get(0);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Updates this data
     */
    public void update() {
        CloudDriver.getInstance().getPermissionPool().updatePlayer(this);
        CloudDriver.getInstance().getPermissionPool().update();
    }

    @Override
    public boolean hasPermission(String permission) {
        return CloudDriver.getInstance().getPermissionPool().hasPermission(this.getUniqueId(), permission);
    }

    @Nullable
    @Override
    public PermissionGroup getCachedPermissionGroup() {
        return CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId());
    }

    @Override
    public PermissionGroup getPermissionGroup() {
        throw new UnsupportedOperationException("Not available for OfflinePlayer");
    }

    @Override
    public void addPermission(String permission) {
        CloudDriver.getInstance().getPermissionPool().addPermissionToUser(this.getUniqueId(), permission);
    }

    @Override
    public void removePermission(String permission) {
        CloudDriver.getInstance().getPermissionPool().removePermissionFromUser(this.getUniqueId(), permission);
    }

    @Override
    public List<String> getExclusivePermissions() {
        return this.permissions;
    }

    @Override
    public List<PermissionGroup> getAllPermissionGroups() {
        return CloudDriver.getInstance().getPermissionPool().getCachedPermissionGroups(this.getUniqueId());
    }

    @Override
    public void removePermissionGroup(PermissionGroup permissionGroup) {
        CloudDriver.getInstance().getPermissionPool().removePermissionGroupFromUser(this.getUniqueId(), permissionGroup);
    }

    @Override
    public void addPermissionGroup(PermissionGroup permissionGroup, int time, PermissionValidity unit) {
        CloudDriver.getInstance().getPermissionPool().addPermissionGroupToUser(this.getUniqueId(), permissionGroup, time, unit);
    }

}
