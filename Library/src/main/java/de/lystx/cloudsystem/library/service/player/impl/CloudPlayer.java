package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.elements.packets.both.inventory.PacketOpenInventory;
import de.lystx.cloudsystem.library.elements.packets.both.player.*;
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
import java.util.UUID;

@Getter @Setter
public class CloudPlayer implements Serializable, CloudCommandSender {

    private Service service;
    private Service proxy;
    
    private final CloudConnection connection;

    private CloudPlayerData cloudPlayerData;
    private LabyModPlayer labyModPlayer;

    public CloudPlayer(CloudConnection connection) {
        this.connection = connection;
        
    }

    public String getName() {
        return this.connection.getName();
    }

    public UUID getUniqueId() {
        return this.connection.getUniqueId();
    }

    public String getIpAddress() {
        return this.connection.getAddress();
    }

    /**
     * Tries to get data
     * > If null returns defaultData
     * @return CloudPlayerData
     */
    public CloudPlayerData getData() {
        return this.cloudPlayerData == null ? Constants.getDefaultData(this.getUniqueId(), this.getName(), this.getIpAddress()) : this.cloudPlayerData;
    }

    /**
     * Returns a CloudPlayer Inventory to manage stuff
     * @return
     */
    public CloudPlayerInventory getInventory() {
        return Constants.INVENTORIES.getOrDefault(this.getUniqueId(), new CloudPlayerInventory(this));
    }

    /**
     * Updates a player and all his data
     */
    public void update() {
        Constants.EXECUTOR.sendPacket(new PacketUpdatePlayer(this.getName(), this));
    }

    /**
     * Gets group
     * @return Highest permissionGroup of player
     */
    public PermissionGroup getPermissionGroup() {
        return Constants.PERMISSION_POOL.getHighestPermissionGroup(this.getName());
    }

    /**
     * Sends a message
     * @param message
     */
    public void sendMessage(Object message) {
        if (Constants.CLOUD_TYPE.equals(CloudType.CLOUDAPI)) {
            Object player;
            if (Constants.SERVICE_TYPE == ServiceType.SPIGOT) {
                player = Reflections.getBukkitPlayer(this.getName());
            } else {
                player = Reflections.getBungeePlayer(this.getName());
            }
            if (message == null) {
                message = "null";
            }
            Reflections.callMethod(player, "sendMessage", message.toString());
            return;
        }
        Constants.EXECUTOR.sendPacket(new PacketSendMessage(this.getUniqueId(), message.toString()));
    }

    /**
     * Sends an actionbar message
     * @param message
     */
    public void sendActionbar(Object message) {
        Constants.EXECUTOR.sendPacket(new PacketSendActionbar(this.getUniqueId(), message.toString()));
    }

    /**
     * Sends a Component
     * @param cloudComponent
     */
    public void sendComponent(CloudComponent cloudComponent) {
        Constants.EXECUTOR.sendPacket(new PacketSendComponent(this.getUniqueId(), cloudComponent));
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
            Object player = Reflections.getBukkitPlayer(this.getName());
            Reflections.callMethod(player, "playSound", sound, v1, v2);
            return;
        }
        Constants.EXECUTOR.sendPacket(new PacketPlaySound(this.getName(), sound.name(), v1, v2));
    }

    /**
     * Sends a title
     * @param title
     * @param subtitle
     */
    public void sendTitle(String title, String subtitle) {
        Constants.EXECUTOR.sendPacket(new PacketSendTitle(this.getName(), title, subtitle));
    }


    /**
     * Sends to fallback
     */
    public void fallback() {
        Constants.EXECUTOR.sendPacket(new PacketFallback(this.getName()));
    }

    /**
     * Connects to a {@link Service}
     * @param service
     */
    public void connect(Service service) {
        Constants.EXECUTOR.sendPacket(new PacketConnectServer(this.getName(), service.getName()));
    }

    /**
     * Sends player to a random service
     * @param serviceGroup
     */
    public void connectRandom(ServiceGroup serviceGroup) {
        Constants.EXECUTOR.sendPacket(new PacketConnectGroup(this.getName(), serviceGroup.getName()));
    }

    /**
     * Kicks for a reason
     * @param reason
     */
    public void kick(String reason) {
        this.getConnection().disconnect(reason);
    }

    /**
     * @param permission
     * @return if has permission
     */
    public boolean hasPermission(String permission) {
        return Constants.PERMISSION_POOL.hasPermission(this.getName(), permission);
    }

    @Deprecated
    public void sendMessage(String prefix, String message) {
        throw new UnsupportedOperationException("Only works on CloudConsole!");
    }
}