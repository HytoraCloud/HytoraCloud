package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.elements.packets.both.*;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.featured.CloudInventory;
import de.lystx.cloudsystem.library.service.player.featured.CloudPlayerInventory;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter @ToString(of = "name")
public class CloudPlayer implements Serializable, CloudCommandSender {

    private final String name;
    private final UUID uniqueId;
    private final String ipAddress;
    private String server;
    private String proxy;
    private CloudPlayerData cloudPlayerData;

    public CloudPlayer(String name, UUID uniqueId, String ipAddress, String server, String proxy) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.ipAddress = ipAddress;
        this.server = server;
        this.proxy = proxy;
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
     * @return serverGroup
     */
    public String getServerGroup() {
        return this.server.split("-")[0];
    }

    /**
     * Sends a message
     * @param message
     */
    public void sendMessage(Object message) {
        PacketSendMessage sendMessage = new PacketSendMessage(this.uniqueId, message.toString());
        Constants.EXECUTOR.sendPacket(sendMessage);
    }

    /**
     * Sends a Component
     * @param cloudComponent
     */
    public void sendComponent(CloudComponent cloudComponent) {
        PacketSendComponent sendMessage = new PacketSendComponent(this.uniqueId, cloudComponent);
        Constants.EXECUTOR.sendPacket(sendMessage);
    }

    /**
     * Opens an inventory to a player
     * @param cloudInventory
     */
    public void openInventory(CloudInventory cloudInventory) {
        PacketOpenInventory packetOpenInventory = new PacketOpenInventory(this, cloudInventory);
        Constants.EXECUTOR.sendPacket(packetOpenInventory);
    }

    /**
     * Plays a Bukkit sound
     * @param sound
     * @param v1
     * @param v2
     */
    public void playSound(Enum<?> sound, float v1, float v2) {
        PacketPlaySound playSound = new PacketPlaySound(this.name, sound.name(), v1, v2);
        Constants.EXECUTOR.sendPacket(playSound);
    }

    /**
     * Sends a title
     * @param title
     * @param subtitle
     */
    public void sendTitle(String title, String subtitle) {
        PacketSendTitle sendTitle = new PacketSendTitle(this.name, title, subtitle);
        Constants.EXECUTOR.sendPacket(sendTitle);
    }

    /**
     * Sends to fallback
     */
    public void fallback() {
        PacketFallback fallback = new PacketFallback(this.name);
        Constants.EXECUTOR.sendPacket(fallback);
    }

    /**
     * Connects to server
     * @param server
     */
    public void connect(String server) {
        PacketConnectServer sendToServer = new PacketConnectServer(this.name, server);
        Constants.EXECUTOR.sendPacket(sendToServer);
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
        this.connect(serviceGroup.getName());
    }

    /**
     * Sends player to a random service
     * @param serviceGroup
     */
    public void connectRandom(String serviceGroup) {
        PacketConnectGroup sendToGroup = new PacketConnectGroup(this.name, serviceGroup);
        Constants.EXECUTOR.sendPacket(sendToGroup);
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
    public void sendMessage(String prefix, String message) {
        this.sendMessage("§8[§b" + prefix + "§8] §7" + message);
    }
}
