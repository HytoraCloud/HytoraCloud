package de.lystx.hytoracloud.driver.service.player.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.chat.CloudComponent;
import de.lystx.hytoracloud.driver.elements.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.elements.packets.both.player.*;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketConnectServer;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPing;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPlayerLocation;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPlayerWorld;
import de.lystx.hytoracloud.driver.elements.packets.request.perms.PacketRequestPermissionGroupGet;
import de.lystx.hytoracloud.driver.elements.packets.request.property.PacketRequestAddProperty;
import de.lystx.hytoracloud.driver.elements.packets.request.property.PacketRequestGetProperty;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.elements.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.elements.world.MinecraftWorld;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.service.player.IPermissionUser;
import de.lystx.hytoracloud.driver.service.player.featured.inventory.CloudInventory;
import de.lystx.hytoracloud.driver.service.player.featured.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.service.util.reflection.Reflections;
import de.lystx.hytoracloud.driver.service.uuid.NameChange;
import de.lystx.hytoracloud.driver.service.uuid.UUIDService;
import lombok.*;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter @Setter
public class CloudPlayer implements Serializable, CloudCommandSender , IPermissionUser, Identifiable {

    /**
     * The Service the Player is on
     */
    private String service;

    /**
     * The proxy the Player is on
     */
    private String proxy;

    /**
     * The connection of the player
     */
    private PlayerConnection connection;

    /**
     * The information of the player
     */
    private PlayerInformation playerInformation;

    public CloudPlayer(PlayerConnection connection) {
        this.connection = connection;
        this.setPlayerInformation(CloudDriver.getInstance().getPermissionPool().loadNonePacketPool().getPlayerInformation(connection.getUniqueId()));
    }

    /**
     * Gets the {@link Service} of this player
     * by searching for a service with the string
     *
     * @return service or null
     */
    public Service getService() {
        return CloudDriver.getInstance().getServiceManager().getService(this.service);
    }

    /**
     * Sets the {@link Service} of this player
     *
     * @param service the service
     */
    public void setService(Service service) {
        this.service = service == null ? "No Server" : service.getName();
    }

    /**
     * Gets the proxy ({@link Service}) of this player
     * by searching for a service with the string
     *
     * @return proxy or null if not found
     */
    public Service getProxy() {
        return CloudDriver.getInstance().getServiceManager().getService(this.proxy);
    }

    /**
     * Sets the proxy ({@link Service}) of this player
     *
     * @param service the proxy
     */
    public void setProxy(Service service) {
        this.proxy = service.getName();
    }

    /**
     * Gets the name of this player
     *
     * @return name
     */
    @Override
    public String getName() {
        return this.connection.getName();
    }

    /**
     * Gets the uuid of this player
     *
     * @return uuid
     */
    @Override
    public UUID getUniqueId() {
        return this.connection.getUniqueId();
    }


    /**
     * Sets the UUID for this {@link CloudCommandSender}
     *
     * @param uniqueId the uuid
     */
    @Override
    public void setUniqueId(UUID uniqueId) {
        throw new UnsupportedOperationException("Not available for CloudPlayer");
    }

    /**
     * Sets the name for this {@link CloudCommandSender}
     *
     * @param name the name
     */
    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not available for CloudPlayer");
    }

    /**
     * Gets the IP Address of this player
     *
     * @return address as string
     */
    public String getIpAddress() {
        return this.connection.getAddress();
    }

    /**
     * Loads the player's ping as {@link Integer}
     * this might take a while because of packet-transfer
     *
     * @return response with the ping
     */
    public int getPing() {
        PacketRequestPing packetRequestPing = new PacketRequestPing(this.getUniqueId());
        Component component = packetRequestPing.toReply(CloudDriver.getInstance().getConnection());
        return Integer.parseInt(component.reply().getMessage());
    }

    /**
     * Loads the player's permissionGroup as {@link PermissionGroup}
     * this might take a while because of packet-transfer
     *
     * @return response with the permissionGroup
     */
    public PermissionGroup getPermissionGroup() {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.BRIDGE)) {
            PacketRequestPermissionGroupGet groupGet = new PacketRequestPermissionGroupGet(this.getUniqueId());

            Component component = groupGet.toReply(CloudDriver.getInstance().getConnection());

            return component.get("group");
        } else {
            return CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId());
        }
    }

    /**
     * Loads the player's property as {@link JsonObject}
     * this might take a while because of packet-transfer
     *
     * @return response with the property
     */
    public JsonObject getProperty(String name) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            Component response = CloudDriver.getInstance().getResponse(new PacketRequestGetProperty(this.getUniqueId(), name));
            return (JsonObject) new JsonParser().parse(response.reply().getMessage());
        } else {
            return CloudDriver.getInstance().getCloudPlayerManager().getOfflinePlayer(this.getUniqueId()).getProperty(name);
        }
    }


    /**
     * Loads all {@link NameChange}s of this player
     * this might take a while because of web requests
     *
     * @return namechange array
     */
    public NameChange[] getNameChanges() {
        return UUIDService.getInstance().getNameChanges(this.getUniqueId());
    }

    /**
     * Checks if this player has ever played before
     *
     * @return boolean
     */
    public boolean hasPlayedBefore() {
        return this.playerInformation.getFirstLogin() == 0L || this.playerInformation.getFirstLogin() == System.currentTimeMillis();
    }

    /**
     * Gets the {@link PlayerInformation} of this player
     *
     * @return information or default if not set
     */
    public PlayerInformation getPlayerInformation() {
        return this.playerInformation == null ? CloudDriver.getInstance().getPermissionPool().getDefaultPlayerInformation(this.getUniqueId(), this.getName(), this.getIpAddress()) : this.playerInformation;
    }

    /**
     * Returns a CloudPlayer Inventory to manage stuff
     *
     * @return inventory if cached or new one
     */
    public CloudPlayerInventory getInventory() {
        return CloudDriver.getInstance().getCloudInventories().getOrDefault(this.getUniqueId(), new CloudPlayerInventory(this));
    }

    /**
     * Updates a player and all his data
     */
    @Override
    public void update() {
        CloudDriver.getInstance().sendPacket(new PacketUpdatePlayer(this));
    }

    /**
     * Gets the {@link PermissionGroup} with the lowest ID (the highest rank)
     *
     * @return group
     */
    @Override
    public PermissionGroup getHighestPermissionGroup() {
        return CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId());
    }

    /**
     * Adds a permission to this player
     *
     * @param permission the permission
     */
    @Override
    public void addPermission(String permission) {
        if (this.playerInformation != null) {
            this.playerInformation.getExclusivePermissions().add(permission);
        }
    }

    /**
     * Removes a permission from a player
     *
     * @param permission the permission
     */
    @Override
    public void removePermission(String permission) {
        if (this.playerInformation != null) {
            this.playerInformation.getExclusivePermissions().remove(permission);
        }
    }

    /**
     * Gets a list of all permissions
     *
     * @return list
     */
    @Override
    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        CloudDriver.getInstance().getPermissionPool().updatePermissions(this.getUniqueId(), this.getIpAddress(), permissions::add);
        return permissions;
    }

    /**
     * Gets a list of permissions only
     * this player has been given
     *
     * @return list of permissions
     */
    @Override
    public List<String> getExclusivePermissions() {
        return this.playerInformation.getPermissions();
    }

    /**
     * Gets all {@link PermissionGroup}s of this player
     *
     * @return list of groups
     */
    @Override
    public List<PermissionGroup> getAllPermissionGroups() {
        return CloudDriver.getInstance().getPermissionPool().getCachedPermissionGroups(this.getUniqueId());
    }

    /**
     * Removes a {@link PermissionGroup} from this player
     *
     * @param permissionGroup the group to be removed
     */
    @Override
    public void removePermissionGroup(PermissionGroup permissionGroup) {
        CloudDriver.getInstance().getPermissionPool().removePermissionGroupFromUser(this.getUniqueId(), permissionGroup);
        CloudDriver.getInstance().getPermissionPool().update();
    }

    /**
     * Adds a {@link PermissionGroup} to this player
     *
     * @param permissionGroup the group to add
     * @param time the time (e.g. "1")
     * @param unit the unit (e.g. "month")
     */
    @Override
    public void addPermissionGroup(PermissionGroup permissionGroup, int time, PermissionValidity unit) {
        CloudDriver.getInstance().getPermissionPool().addPermissionGroupToUser(this.getUniqueId(), permissionGroup, time, unit);
        CloudDriver.getInstance().getPermissionPool().update();
    }

    /**
     * Sends a message to this player
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(Object message) {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.BRIDGE)) {
            Object player = Reflections.getPlayer(this.getName());
            if (player == null) {
                return;
            }
            if (message == null) {
                message = "null";
            }
            Reflections.callMethod(player, "sendMessage", message.toString());
            return;
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendMessage(this.getUniqueId(), message.toString()));
    }

    /**
     * Sends an action bar message to
     * this player
     *
     * @param message the message to send
     */
    public void sendActionbar(Object message) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendActionbar(this.getUniqueId(), message.toString()));
    }

    /**
     * Sends a {@link CloudComponent} to this player
     *
     * @param cloudComponent the component to send
     */
    @Override
    public void sendComponent(CloudComponent cloudComponent) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendComponent(this.getUniqueId(), cloudComponent));
    }

    /**
     * Opens a {@link CloudInventory} to this player
     *
     * @param cloudInventory the inventory to open
     */
    public void openInventory(CloudInventory cloudInventory) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketOpenInventory(this, cloudInventory));
    }

    @SneakyThrows
    public void playSound(Enum<?> sound, Float v1, Float v2) {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.BRIDGE) && CloudDriver.getInstance().getThisService().getServiceGroup().getServiceType() == ServiceType.SPIGOT) {
            Object player = Reflections.getBukkitPlayer(this.getName());
            Reflections.callMethod(player, "playSound", sound, v1, v2);
            return;
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketPlaySound(this.getName(), sound.name(), v1, v2));
    }

    /**
     * Sends a title to this player
     *
     * @param title the title
     * @param subtitle the subtitle
     */
    public void sendTitle(String title, String subtitle) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendTitle(this.getName(), title, subtitle));
    }

    /**
     * Adds a property to this player
     *
     * @param name the name of the property
     * @param jsonObject the data
     * @return status
     */
    public ResponseStatus addProperty(String name, JsonObject jsonObject) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            return CloudDriver.getInstance().getResponse(new PacketRequestAddProperty(this.getUniqueId(), name, jsonObject)).reply().getStatus();
        }
        playerInformation.addProperty(name, jsonObject);
        playerInformation.update();
        return ResponseStatus.SUCCESS;
    }

    /**
     * Fallbacks this player
     */
    public void fallback() {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketFallback(this.getUniqueId()));
    }

    /**
     * Connects this player to a {@link Service}
     *
     * @param service the service to connect to
     */
    public void connect(Service service) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketConnectServer(this.getUniqueId(), service.getName()));
    }

    /**
     * Connects this player to a random service
     * out of the given group
     *
     * @param serviceGroup the group
     */
    public void connectRandom(ServiceGroup serviceGroup) {

        List<Service> services = CloudDriver.getInstance().getServiceManager().getServices(serviceGroup);
        Service service = services.get(new Random().nextInt(services.size()));

        this.connect(service);
    }

    /**
     * Kicks this player from the network
     *
     * @param reason the reason for the kick
     */
    public void kick(String reason) {
        this.getConnection().disconnect(reason);
    }

    /**
     * Checks if a player has a permission
     *
     * @param permission the permission to check
     * @return boolean
     */
    @Override
    public boolean hasPermission(String permission) {
        return CloudDriver.getInstance().getPermissionPool().hasPermission(this.getUniqueId(), permission);
    }

    /**
     * Loads the cached {@link PermissionGroup} of this player
     *
     * @return group
     */
    @Nullable
    @Override
    public PermissionGroup getCachedPermissionGroup() {
        return CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId());
    }

    /**
     * Sends a message with prefix
     *
     * @param prefix the prefix
     * @param message the message
     */
    @Override
    public void sendMessage(String prefix, String message) {
        throw new UnsupportedOperationException("Only works on CloudConsole!");
    }

    /**
     * Easier method to get a {@link CloudPlayer}
     * by its name (cached)
     *
     * @param name the name of the player
     * @return player or null if not cached
     */
    public static CloudPlayer fromName(String name) {
        return CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(name);
    }

    /**
     * Easier method to get a {@link CloudPlayer}
     * by its uuid (cached)
     *
     * @param uniqueId the uuid of the player
     * @return player or null if not cached
     */
    public static CloudPlayer fromUUID(UUID uniqueId) {
        return CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(uniqueId);
    }
}