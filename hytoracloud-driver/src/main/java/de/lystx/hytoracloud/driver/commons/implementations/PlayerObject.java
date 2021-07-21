package de.lystx.hytoracloud.driver.commons.implementations;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionEntry;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Inventory;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.CloudComponent;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerUpdate;
import de.lystx.hytoracloud.driver.commons.packets.both.player.*;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketConnectServer;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestPing;
import de.lystx.hytoracloud.driver.commons.packets.in.request.perms.PacketRequestPermissionGroupGet;
import de.lystx.hytoracloud.driver.commons.packets.in.request.property.PacketRequestAddProperty;
import de.lystx.hytoracloud.driver.commons.packets.in.request.property.PacketRequestGetProperty;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.utils.reflection.Reflections;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.utils.uuid.NameChange;
import de.lystx.hytoracloud.driver.utils.uuid.UUIDService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter @Setter
public class PlayerObject extends WrappedObject<ICloudPlayer, PlayerObject> implements ICloudPlayer {

    private static final long serialVersionUID = 7250454458770916643L;

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
    private OfflinePlayer offlinePlayer;

    public PlayerObject(PlayerConnection connection) {
        this.connection = connection;
        this.setOfflinePlayer(CloudDriver.getInstance().getPermissionPool().getCachedObject(connection.getUniqueId()));
    }
    @Override
    public IService getService() {
        return CloudDriver.getInstance().getServiceManager().getCachedObject(this.service);
    }

    @Override
    public IService getProxy() {
        return CloudDriver.getInstance().getServiceManager().getCachedObject(this.proxy);
    }

    @Override
    public void setService(IService service) {
        this.service = service == null ? "No Server" : service.getName();
    }

    @Override
    public void setProxy(IService proxy) {
        this.proxy = proxy.getName();
    }


    @Override
    public String getName() {
        return this.connection.getName();
    }

    @Override
    public UUID getUniqueId() {
        return this.connection.getUniqueId();
    }

    @Override
    public void setUniqueId(UUID uniqueId) {
        throw new UnsupportedOperationException("Not available for CloudPlayer");
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not available for CloudPlayer");
    }

    @Override
    public String getIpAddress() {
        return this.connection.getAddress();
    }

    @Override
    public int getPing() {
        if (CloudDriver.getInstance().isBridge() && CloudDriver.getInstance().getCurrentService().getGroup().getType() == ServiceType.PROXY) {
            return (int) CloudDriver.getInstance().getProxyBridge().getPing(this.getUniqueId());
        }
        PacketRequestPing packetRequestPing = new PacketRequestPing(this.getUniqueId());
        Component component = packetRequestPing.toReply(CloudDriver.getInstance().getConnection());
        return Integer.parseInt(component.reply().getMessage());
    }

    @Override
    public PermissionGroup getPermissionGroup() {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.BRIDGE)) {
            PacketRequestPermissionGroupGet groupGet = new PacketRequestPermissionGroupGet(this.getUniqueId());

            Component component = groupGet.toReply(CloudDriver.getInstance().getConnection());

            return component.get("group");
        } else {
            return CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId());
        }
    }

    @Override
    public PropertyObject getProperty(String name) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            Component response = CloudDriver.getInstance().getResponse(new PacketRequestGetProperty(this.getUniqueId(), name));
            return PropertyObject.fromDocument(new JsonEntity(response.reply().getMessage()));
        } else {
            return CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(this.getUniqueId()).getProperty(name);
        }
    }

    @Override
    public NameChange[] getNameChanges() {
        return UUIDService.getInstance().getNameChanges(this.getUniqueId());
    }

    @Override
    public boolean hasPlayedBefore() {
        return this.offlinePlayer.getFirstLogin() == 0L || this.offlinePlayer.getFirstLogin() == System.currentTimeMillis();
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        if (this.offlinePlayer == null) {
            this.offlinePlayer = new OfflinePlayer(this.getUniqueId(), this.getName(), Collections.singletonList(new PermissionEntry(CloudDriver.getInstance().getPermissionPool().getDefaultPermissionGroup().getName(), "")), new LinkedList<>(), this.getIpAddress(), true, true, new Date().getTime(), 0L, new HashMap<>());
        }
        return this.offlinePlayer;
    }

    @Override
    public CloudPlayerInventory getInventory() {
        return CloudDriver.getInstance().getCloudInventories().getOrDefault(this.getUniqueId(), new CloudPlayerInventory(this));
    }

    @Override
    public void update() {

        DriverEventPlayerUpdate playerUpdate = new DriverEventPlayerUpdate(this);
        CloudDriver.getInstance().callEvent(playerUpdate);

        PacketUpdatePlayer packetUpdatePlayer = new PacketUpdatePlayer(this);
        CloudDriver.getInstance().sendPacket(packetUpdatePlayer);
    }

    @Override
    public PermissionGroup getHighestPermissionGroup() {
        return CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId());
    }

    @Override
    public void addPermission(String permission) {
        if (this.offlinePlayer != null) {
            this.offlinePlayer.getExclusivePermissions().add(permission);
        }
    }

    @Override
    public void removePermission(String permission) {
        if (this.offlinePlayer != null) {
            this.offlinePlayer.getExclusivePermissions().remove(permission);
        }
    }

    @Override
    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        CloudDriver.getInstance().getPermissionPool().updatePermissions(this.getUniqueId(), this.getIpAddress(), permissions::add);
        return permissions;
    }

    @Override
    public List<String> getExclusivePermissions() {
        return this.offlinePlayer.getPermissions();
    }

    @Override
    public List<PermissionGroup> getAllPermissionGroups() {
        return CloudDriver.getInstance().getPermissionPool().getPermissionGroups(this.getUniqueId());
    }

    @Override
    public void removePermissionGroup(PermissionGroup permissionGroup) {
        CloudDriver.getInstance().getPermissionPool().removePermissionGroupFromUser(this.getUniqueId(), permissionGroup);
        CloudDriver.getInstance().getPermissionPool().update();
    }

    @Override
    public void addPermissionGroup(PermissionGroup permissionGroup, int time, PermissionValidity unit) {
        CloudDriver.getInstance().getPermissionPool().addPermissionGroupToUser(this.getUniqueId(), permissionGroup, time, unit);
        CloudDriver.getInstance().getPermissionPool().update();
    }

    @Override
    public void sendMessage(Object message) {
        if (CloudDriver.getInstance().isBridge()) {
            if (CloudDriver.getInstance().getCurrentService().getGroup().getType() == ServiceType.PROXY) {
                CloudDriver.getInstance().getProxyBridge().messagePlayer(this.getUniqueId(), message.toString());
            } else {
                Object player = Reflections.getBukkitPlayer(this.getName());
                Reflections.callMethod(player, "sendMessage", message.toString());
            }
        } else {
            CloudDriver.getInstance().getConnection().sendPacket(new PacketSendMessage(this.getUniqueId(), message.toString()));
        }
    }

    @Override
    public void sendActionbar(Object message) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendActionbar(this.getUniqueId(), message.toString()));
    }

    @Override
    public void sendComponent(CloudComponent cloudComponent) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendComponent(this.getUniqueId(), cloudComponent));
    }

    @Override
    public void openInventory(Inventory inventory) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketOpenInventory(this, (InventoryObject) inventory));
    }

    @SneakyThrows @Override
    public void playSound(Enum<?> sound, Float v1, Float v2) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketPlaySound(this.getName(), sound.name(), v1, v2));
    }

    @Override
    public void sendTitle(String title, String subtitle) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendTitle(this.getName(), title, subtitle));
    }

    @Override
    public ResponseStatus addProperty(String name, PropertyObject jsonObject) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            return CloudDriver.getInstance().getResponse(new PacketRequestAddProperty(this.getUniqueId(), name, jsonObject)).reply().getStatus();
        }
        offlinePlayer.addProperty(name, jsonObject);
        offlinePlayer.update();
        return ResponseStatus.SUCCESS;
    }

    @Override
    public void fallback() {

        if (CloudDriver.getInstance().isBridge() && CloudDriver.getInstance().getProxyBridge() != null) {
            CloudDriver.getInstance().getProxyBridge().fallbackPlayer(this.getUniqueId());
            return;
        }

        CloudDriver.getInstance().getConnection().sendPacket(new PacketFallback(this.getUniqueId()));
    }

    @Override
    public void connect(IService service) {
        if (CloudDriver.getInstance().isBridge() && CloudDriver.getInstance().getProxyBridge() != null) {
            CloudDriver.getInstance().getProxyBridge().connectPlayer(this.getUniqueId(), service.getName());
            return;
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketConnectServer(this.getUniqueId(), service.getName()));
    }

    @Override
    public void connectRandom(IServiceGroup serviceGroup) {

        List<IService> services = CloudDriver.getInstance().getServiceManager().getServices(serviceGroup);
        IService service = services.get(new Random().nextInt(services.size()));

        this.connect(service);
    }

    @Override
    public void kick(String reason) {
        if (CloudDriver.getInstance().isBridge() && CloudDriver.getInstance().getProxyBridge() != null) {
            CloudDriver.getInstance().getProxyBridge().kickPlayer(this.getUniqueId(), reason);
            return;
        }
        this.getConnection().disconnect(reason);
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
    public void sendMessage(String prefix, String message) {
        throw new UnsupportedOperationException("Only works on CloudConsole!");
    }

    @Override
    Class<PlayerObject> getWrapperClass() {
        return PlayerObject.class;
    }

    @Override
    Class<ICloudPlayer> getInterface() {
        return ICloudPlayer.class;
    }
}