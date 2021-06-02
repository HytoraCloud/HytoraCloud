package de.lystx.hytoracloud.driver.service.permission.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.CloudDriver;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter @AllArgsConstructor
public class PermissionGroup implements Serializable, ThunderObject {

    /**
     * The name of the group (e.g. "Admin")
     */
    private String name;

    /**
     * The id to sort in tablist (e.g. 0)
     * [The lower the ID the higher the rank)
     */
    private Integer id;

    /**
     * The prefix of this rank
     */
    private String prefix;

    /**
     * The suffix of this rank
     */
    private String suffix;

    /**
     * The display (colorCode) of this rank (e.g. "§4" or "&4")
     */
    private String display;

    /**
     * The custom chat format for this rank only
     */
    private String chatFormat;

    /**
     * The permissions this rank has
     */
    private List<String> permissions;

    /**
     * The inheritances this group has
     * (It extends all permissions from the inheritances)
     */
    private List<String> inheritances;

    /**
     * The properties this group has to store
     * extra values (e.g. store a TeamSpeak Rank for this Rank)
     */
    private Map<String, JsonObject> properties;


    /**
     * Adds a Permission to this group
     *
     * @param permission the permission to add
     */
    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    /**
     * Removes a Permission from this group
     *
     * @param permission the permission to remove
     */
    public void removePermission(String permission) {
        this.permissions.remove(permission);
    }

    /**
     * Gets all {@link PermissionGroup} inheritances
     *
     * @return list of groups
     */
    public List<PermissionGroup> getInheritances() {
        List<PermissionGroup> list = new ArrayList<>();
        for (String inheritance : inheritances) {
            list.add(CloudDriver.getInstance().getPermissionPool().getPermissionGroupByName(inheritance));
        }
        return list;
    }

    /**
     * Creates a list containing all permissions this rank has
     * it iterates through all the inheritances and add their
     * permissions also and return it
     *
     * @return list of permissions
     */
    public List<String> getAllPermissions() {
        List<String> list = new ArrayList<>();
        for (PermissionGroup inheritance : this.getInheritances()) {
            list.addAll(inheritance.getPermissions());
        }
        list.addAll(permissions);
        return list;
    }

    /**
     * Adds a property to this group to store some values
     *
     * @param name the name of the property (e.g. "teamSpeakID")
     * @param property the property (e.g. "148)
     */
    public void addProperty(String name, JsonObject property) {
        this.properties.put(name, property);
    }


    /**
     * Updates the current group
     * and syncs it all over the network
     */
    public void update() {

        //Replacing old group with updated
        PermissionGroup group = CloudDriver.getInstance().getPermissionPool().getPermissionGroupByName(this.name);
        CloudDriver.getInstance().getPermissionPool().getCachedPermissionGroups().set(CloudDriver.getInstance().getPermissionPool().getCachedPermissionGroups().indexOf(group), this);
        CloudDriver.getInstance().getPermissionPool().update();
    }


    @Override
    public void write(PacketBuffer buf) {

        buf.writeString(name); //Writes the name
        buf.writeInt(id); //writes the id
        buf.writeString(prefix); //writes the prefix
        buf.writeString(suffix); //writes the suffix
        buf.writeString(display); //writes the display
        buf.writeString(chatFormat); //writes the chatFormat

        //Writes the permissions
        buf.writeInt(permissions.size()); //the size of the list
        for (String permission : permissions) {
            //the permissions
            buf.writeString(permission);
        }

        //Writes the inheritances
        buf.writeInt(inheritances.size()); //size of the inheritance list
        for (String inheritance : inheritances) {
            //the inheritances
            buf.writeString(inheritance);
        }

        //writes the properties
        buf.writeString(properties.toString());

        buf.writeInt(properties.size());
        properties.forEach((s, json) -> {
            buf.writeString(s);
            buf.writeString(json.toString());
        });
    }

    @Override
    public void read(PacketBuffer buf) {
        name = buf.readString(); //the name
        id = buf.readInt(); //the id
        prefix = buf.readString(); //the prefix
        suffix = buf.readString(); //the suffix
        display = buf.readString(); //the display
        chatFormat = buf.readString(); //the chatFormat

        int permsSize = buf.readInt(); //the size of the permissions
        permissions = new ArrayList<>(permsSize); //the permissionsList
        for (int i1 = 0; i1 < permsSize; i1++) {
            permissions.add(buf.readString());
        }

        int inSize = buf.readInt(); //the size of the inheritances
        inheritances = new ArrayList<>(inSize); //the inheritance list
        for (int i1 = 0; i1 < inSize; i1++) {
            inheritances.add(buf.readString());
        }

        int propSize = buf.readInt();
        properties = new HashMap<>(propSize);
        for (int i = 0; i < propSize; i++) {
            properties.put(buf.readString(), (JsonObject) new JsonParser().parse(buf.readString()));
        }

    }
}
