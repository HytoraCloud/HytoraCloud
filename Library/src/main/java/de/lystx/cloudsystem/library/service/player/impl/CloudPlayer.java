package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.Cloud;
import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.elements.packets.both.inventory.PacketOpenInventory;
import de.lystx.cloudsystem.library.elements.packets.both.player.*;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionValidality;
import de.lystx.cloudsystem.library.service.player.featured.inventory.CloudInventory;
import de.lystx.cloudsystem.library.service.player.featured.inventory.CloudPlayerInventory;
import de.lystx.cloudsystem.library.service.player.featured.labymod.LabyModPlayer;
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
        this.service = connection.getStart();
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
     * Checks if the player has played before
     * @return
     */
    public boolean hasPlayedBefore() {
        return this.cloudPlayerData.getFirstLogin() == 0L || this.cloudPlayerData.getFirstLogin() == System.currentTimeMillis();
    }

    /**
     * Tries to get data
     * > If null returns defaultData
     * @return CloudPlayerData
     */
    public CloudPlayerData getData() {
        return this.cloudPlayerData == null ? Cloud.getInstance().getPermissionPool().getDefaultData(this.getUniqueId(), this.getName(), this.getIpAddress()) : this.cloudPlayerData;
    }


    public void addPermissionGroup(String group, int i, PermissionValidality validality) {
        PermissionPool pool = Cloud.getInstance().getPermissionPool();
        pool.updatePermissionGroup(this.getName(), pool.getPermissionGroupFromName(group), i, validality);
        pool.update();
    }

    /**
     * Returns a CloudPlayer Inventory to manage stuff
     * @return
     */
    public CloudPlayerInventory getInventory() {
        return Cloud.getInstance().getCloudInventories().getOrDefault(this.getUniqueId(), new CloudPlayerInventory(this));
    }

    /**
     * Updates a player and all his data
     */
    public void update() {
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketUpdatePlayer(this.getName(), this));
    }

    /**
     * Gets group
     * @return Highest permissionGroup of player
     */
    public PermissionGroup getPermissionGroup() {
        return Cloud.getInstance().getPermissionPool().getHighestPermissionGroup(this.getName());
    }

    /**
     * Sends a message
     * @param message
     */
    public void sendMessage(Object message) {
        if (Cloud.getInstance().getCurrentCloudType().equals(CloudType.CLOUDAPI)) {
            Object player;
            if (Cloud.getInstance().getCurrentServiceType() == ServiceType.SPIGOT) {
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
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketSendMessage(this.getUniqueId(), message.toString()));
    }

    /**
     * Sends an actionbar message
     * @param message
     */
    public void sendActionbar(Object message) {
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketSendActionbar(this.getUniqueId(), message.toString()));
    }

    /**
     * Sends a Component
     * @param cloudComponent
     */
    public void sendComponent(CloudComponent cloudComponent) {
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketSendComponent(this.getUniqueId(), cloudComponent));
    }

    /**
     * Opens an inventory to a player
     * @param cloudInventory
     */
    public void openInventory(CloudInventory cloudInventory) {
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketOpenInventory(this, cloudInventory));
    }

    /**
     * Plays a Bukkit sound
     * @param sound
     * @param v1
     * @param v2
     */
    @SneakyThrows
    public void playSound(Enum<?> sound, Float v1, Float v2) {
        if (Cloud.getInstance().getCurrentCloudType().equals(CloudType.CLOUDAPI) && Cloud.getInstance().getCurrentServiceType() == ServiceType.SPIGOT) {
            Object player = Reflections.getBukkitPlayer(this.getName());
            Reflections.callMethod(player, "playSound", sound, v1, v2);
            return;
        }
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketPlaySound(this.getName(), sound.name(), v1, v2));
    }

    /**
     * Sends a title
     * @param title
     * @param subtitle
     */
    public void sendTitle(String title, String subtitle) {
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketSendTitle(this.getName(), title, subtitle));
    }


    /**
     * Sends to fallback
     */
    public void fallback() {
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketFallback(this.getName()));
    }

    /**
     * Connects to a {@link Service}
     * @param service
     */
    public void connect(Service service) {
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketConnectServer(this.getName(), service.getName()));
    }

    /**
     * Sends player to a random service
     * @param serviceGroup
     */
    public void connectRandom(ServiceGroup serviceGroup) {
        Cloud.getInstance().getCurrentCloudExecutor().sendPacket(new PacketConnectGroup(this.getName(), serviceGroup.getName()));
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
        return Cloud.getInstance().getPermissionPool().hasPermission(this.getName(), permission);
    }

    @Deprecated
    public void sendMessage(String prefix, String message) {
        throw new UnsupportedOperationException("Only works on CloudConsole!");
    }
}