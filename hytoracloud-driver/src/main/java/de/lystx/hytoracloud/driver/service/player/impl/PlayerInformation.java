package de.lystx.hytoracloud.driver.service.player.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionEntry;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.service.player.IPermissionUser;
import io.thunder.packet.PacketBuffer;
import io.thunder.packet.impl.response.IResponse;
import io.thunder.utils.objects.ThunderObject;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//TODO: DOCUMENTATION (AGAIN)
@Getter @Setter @AllArgsConstructor
public class PlayerInformation implements ThunderObject, IPermissionUser, Objectable<PlayerInformation> {

    private UUID uniqueId;
    private String name;
    private List<PermissionEntry> permissionEntries;
    private List<String> permissions;
    private String ipAddress;
    private boolean notifyServerStart;
    private boolean isDefault;
    private long firstLogin;
    private long lastLogin;
    private Map<String, JsonObject> properties;

    public PlayerInformation(UUID uniqueId, String name, List<PermissionEntry> permissionEntries, List<String> permissions, String ipAddress, boolean notifyServerStart, long firstLogin, long lastLogin) {
        this(uniqueId, name, permissionEntries, permissions, ipAddress, notifyServerStart, false, firstLogin, lastLogin, new HashMap<>());
    }

    /**
     * Adds a property to this data
     * @param name the name (e.g "global")
     * @param data the data (e.g. coins or something)
     */
    public void addProperty(String name, JsonObject data) {
        this.properties.put(name, data);
    }

    /**
     * Searches for a property with a given name
     *
     * @param name the name of the property
     * @return jsonObject
     */
    public JsonObject getProperty(String name) {
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
        CloudDriver.getInstance().getPermissionPool().update(); //TODO: Fix work after one update();
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
    public IResponse<PermissionGroup> getPermissionGroup() {
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
    public void write(PacketBuffer buf) {

        if (getPermissionEntries() == null) {
            setPermissionEntries(new ArrayList<>());
        }

        if (getPermissions() == null) {
            setPermissions(new ArrayList<>());
        }

        if (getProperties() == null) {
            setProperties(new HashMap<>());
        }

        buf.nullSafe().writeUUID(getUniqueId());
        buf.nullSafe().writeString(getName());

        buf.nullSafe().writeInt(getPermissionEntries().size());
        for (PermissionEntry permissionEntry : getPermissionEntries()) {
            buf.nullSafe().writeString(permissionEntry.getPermissionGroup());
            buf.nullSafe().writeString(permissionEntry.getValidTime());
        }

        buf.nullSafe().writeInt(getPermissions().size());
        for (String permission : getPermissions()) {
            buf.nullSafe().writeString(permission);
        }

        buf.nullSafe().writeString(getIpAddress());

        buf.nullSafe().writeBoolean(isNotifyServerStart());
        buf.nullSafe().writeBoolean(isDefault());
        buf.nullSafe().writeLong(getFirstLogin());
        buf.nullSafe().writeLong(getLastLogin());

        buf.nullSafe().writeInt(getProperties().keySet().size());
        for (String s : getProperties().keySet()) {
            buf.nullSafe().writeString(s);
            buf.nullSafe().writeString(getProperties().get(s).toString());
        }
    }

    @Override
    public void read(PacketBuffer buf) {

        setUniqueId(buf.nullSafe().readUUID());
        setName(buf.nullSafe().readString());

        int size = buf.nullSafe().readInt();
        setPermissionEntries(new ArrayList<>(size));
        for (int i = 0; i < size; i++) {
            String group = buf.nullSafe().readString();
            String time = buf.nullSafe().readString();
            getPermissionEntries().add(new PermissionEntry(group, time));
        }

        size = buf.nullSafe().readInt();
        setPermissions(new ArrayList<>());
        for (int i = 0; i < size; i++) {
            getPermissions().add(buf.nullSafe().readString());
        }
        setIpAddress(buf.nullSafe().readString());
        setNotifyServerStart(buf.nullSafe().readBoolean());
        setDefault(buf.nullSafe().readBoolean());
        setFirstLogin(buf.nullSafe().readLong());
        setLastLogin(buf.nullSafe().readLong());

        size = buf.nullSafe().readInt();
        setProperties(new HashMap<>(size));

        for (int i = 0; i < size; i++) {
            String s = buf.nullSafe().readString();
            JsonObject object = (JsonObject) new JsonParser().parse(buf.nullSafe().readString());
            getProperties().put(s, object);
        }

    }
}
