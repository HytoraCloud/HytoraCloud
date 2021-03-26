package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.elements.packets.both.*;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.featured.inventory.CloudInventory;
import de.lystx.cloudsystem.library.service.player.featured.inventory.CloudPlayerInventory;
import de.lystx.cloudsystem.library.service.player.featured.labymod.LabyModPlayer;
import de.lystx.cloudsystem.library.service.util.Constants;
import de.lystx.cloudsystem.library.service.util.Reflections;
import lombok.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.UUID;

@Getter @Setter @ToString(of = "name")
public class CloudPlayer implements Serializable, CloudCommandSender {

    private final String name;
    private final UUID uniqueId;
    private final String ipAddress;
    private String server;
    private String proxy;
    private CloudPlayerData cloudPlayerData;
    private LabyModPlayer labyModPlayer;

    public CloudPlayer(String name, UUID uniqueId, String ipAddress, String server, String proxy) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.ipAddress = ipAddress;
        this.server = server;
        this.proxy = proxy;
    }

    public Service getConnectedService() {
        return Constants.SERVICE_FILTER.find(service -> service.getName().equalsIgnoreCase(this.server)).findFirst().orElse(null).get();
    }

    /**
     * Tries to get data
     * > If null returns defaultData
     * @return CloudPlayerData
     */
    public CloudPlayerData getCloudPlayerData() {
        return this.cloudPlayerData == null ? new DefaultCloudPlayerData(this.uniqueId, this.name, this.ipAddress) : this.cloudPlayerData;
    }

    /**
     * Returns a CloudPlayer Inventory to manage stuff
     * @return
     */
    public CloudPlayerInventory getInventory() {
        return Constants.INVENTORIES.getOrDefault(this.uniqueId, new CloudPlayerInventory(this));
    }

    /**
     * Updates a player and all his data
     */
    public void update() {
        Constants.EXECUTOR.sendPacket(new PacketUpdatePlayer(this.name, this));
    }

    /**
     * Gets group
     * @return Highest permissionGroup of player
     */
    public PermissionGroup getPermissionGroup() {
        return Constants.PERMISSION_POOL.getHighestPermissionGroup(this.name);
    }

    /**
     * Sends a message
     * @param message
     */
    public void sendMessage(Object message) {
        if (Constants.CLOUD_TYPE.equals(CloudType.CLOUDAPI)) {
            Object player;
            if (Constants.SERVICE_TYPE == ServiceType.SPIGOT) {
                player = Reflections.getBukkitPlayer(this.name);
            } else {
                player = Reflections.getBungeePlayer(this.name);
            }
            Reflections.callMethod(player, "sendMessage", message);
            return;
        }
        Constants.EXECUTOR.sendPacket(new PacketSendMessage(this.uniqueId, message.toString()));
    }

    public void sendActionbar(Object message) {
        Constants.EXECUTOR.sendPacket(new PacketSendActionbar(this.uniqueId, message.toString()));
    }

    /**
     * Sends a Component
     * @param cloudComponent
     */
    public void sendComponent(CloudComponent cloudComponent) {
        Constants.EXECUTOR.sendPacket(new PacketSendComponent(this.uniqueId, cloudComponent));
    }

    /**
     * Opens an inventory to a player
     * @param cloudInventory
     */
    public void openInventory(CloudInventory cloudInventory) {
        Constants.EXECUTOR.sendPacket(new PacketOpenInventory(this, cloudInventory));
    }

    /**
     * Plays a Bukkit sound
     * @param sound
     * @param v1
     * @param v2
     */
    @SneakyThrows
    public void playSound(Enum<?> sound, Float v1, Float v2) {
        if (Constants.CLOUD_TYPE.equals(CloudType.CLOUDAPI) && Constants.SERVICE_TYPE == ServiceType.SPIGOT) {
            Object player = Reflections.getBukkitPlayer(this.name);
            Reflections.callMethod(player, "playSound", sound, v1, v2);
            return;
        }
        Constants.EXECUTOR.sendPacket(new PacketPlaySound(this.name, sound.name(), v1, v2));
    }

    /**
     * Sends a title
     * @param title
     * @param subtitle
     */
    public void sendTitle(String title, String subtitle) {
        Constants.EXECUTOR.sendPacket(new PacketSendTitle(this.name, title, subtitle));
    }


    /**
     * Sends to fallback
     */
    public void fallback() {
        Constants.EXECUTOR.sendPacket(new PacketFallback(this.name));
    }

    /**
     * Connects to server
     * @param server
     */
    public void connect(String server) {
        Constants.EXECUTOR.sendPacket(new PacketConnectServer(this.name, server));
    }

    /**
     * Connects to a {@link Service}
     * @param service
     */
    public void connect(Service service) {
        this.connect(service.getName());
    }

    /**
     * Sends player to a random service
     * @param serviceGroup
     */
    public void connectRandom(ServiceGroup serviceGroup) {
        this.connectRandom(serviceGroup.getName());
    }

    /**
     * Sends player to a random service
     * @param serviceGroup
     */
    public void connectRandom(String serviceGroup) {
        Constants.EXECUTOR.sendPacket(new PacketConnectGroup(this.name, serviceGroup));
    }

    /**
     * Kicks for a reason
     * @param reason
     */
    public void kick(String reason) {
        this.createConnection().disconnect(reason);
    }

    /**
     * @param permission
     * @return if has permission
     */
    public boolean hasPermission(String permission) {
        return Constants.PERMISSION_POOL.hasPermission(this.name, permission);
    }

    /**
     * Creates connection
     * @return CloudConnection
     */
    public CloudConnection createConnection() {
        return new CloudConnection(this.uniqueId, this.name, this.ipAddress);
    }

    /**
     * Sends message with prefix (unused)
     * @param prefix
     * @param message
     */
    @Deprecated
    public void sendMessage(String prefix, String message) {
        this.sendMessage("§8[§b" + prefix + "§8] §7" + message);
    }
}