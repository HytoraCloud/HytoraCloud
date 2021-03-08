package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.elements.packets.communication.*;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketState;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.featured.CloudInventory;
import de.lystx.cloudsystem.library.service.player.featured.CloudPlayerInventory;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

@Getter @Setter @ToString
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
        Constants.EXECUTOR.sendPacket(new PacketCommunicationUpdateCloudPlayer(this.name, this));
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
    public void sendMessage(String message) {
        PacketCommunicationSendMessage sendMessage = new PacketCommunicationSendMessage(this.uniqueId, message);
        Constants.EXECUTOR.sendPacket(sendMessage);
    }

    /**
     * Sends a Component
     * @param cloudComponent
     */
    public void sendComponent(CloudComponent cloudComponent) {
        PacketCommunicationSendComponent sendMessage = new PacketCommunicationSendComponent(this.uniqueId, cloudComponent);
        Constants.EXECUTOR.sendPacket(sendMessage);
    }

    /**
     * Opens an inventory to a player
     * @param cloudInventory
     */
    public void openInventory(CloudInventory cloudInventory) {
        PacketCommunicationOpenInventory packetCommunicationOpenInventory = new PacketCommunicationOpenInventory(this, cloudInventory);
        Constants.EXECUTOR.sendPacket(packetCommunicationOpenInventory);
    }

    /**
     * Plays a Bukkit sound
     * @param sound
     * @param v1
     * @param v2
     */
    public void playSound(Enum<?> sound, float v1, float v2) {
        PacketCommunicationPlaySound playSound = new PacketCommunicationPlaySound(this.name, sound.name(), v1, v2);
        Constants.EXECUTOR.sendPacket(playSound);
    }

    /**
     * Sends a title
     * @param title
     * @param subtitle
     */
    public void sendTitle(String title, String subtitle) {
        PacketCommunicationSendTitle sendTitle = new PacketCommunicationSendTitle(this.name, title, subtitle);
        Constants.EXECUTOR.sendPacket(sendTitle);
    }

    /**
     * Sends to fallback
     */
    public void fallback() {
        PacketCommunicationFallback fallback = new PacketCommunicationFallback(this.name);
        Constants.EXECUTOR.sendPacket(fallback);
    }

    /**
     * Connects to server
     * @param server
     */
    public void connect(String server) {
        PacketCommunicationSendToServer sendToServer = new PacketCommunicationSendToServer(this.name, server);
        Constants.EXECUTOR.sendPacket(sendToServer);
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
