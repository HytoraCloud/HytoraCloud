package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionEntry;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Inventory;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerUpdate;
import de.lystx.hytoracloud.driver.commons.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.commons.packets.both.player.*;
import de.lystx.hytoracloud.driver.commons.requests.base.SimpleQuery;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.requests.base.IQuery;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.uuid.NameChange;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
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
    public IQuery<Integer> getPing() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            return IQuery.dummy("PLAYER_GET_PING", (int) CloudDriver.getInstance().getBridgeInstance().getPing(this.getUniqueId()));
        }
        DriverRequest<Integer> request = DriverRequest.create("PLAYER_GET_PING", Integer.class);
        request.append("uniqueId", this.getUniqueId().toString());
        return request.execute();
    }

    @Override
    public IQuery<PermissionGroup> getPermissionGroup() {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.BRIDGE)) {

            DriverRequest<PermissionGroup> request = DriverRequest.create("PLAYER_GET_PERMISSIONGROUP", "CLOUD", PermissionGroup.class);
            request.append("uniqueId", this.getUniqueId().toString());
            return request.execute();
        } else {
            return IQuery.dummy("PLAYER_GET_PERMISSIONGROUP", CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId()));
        }
    }

    @Override
    public IQuery<PropertyObject> getProperty(String name) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            DriverRequest<PropertyObject> request = DriverRequest.create("PLAYER_GET_PROPERTY", PropertyObject.class);
            request.append("uniqueId", this.getUniqueId().toString());
            request.append("name", name);
            return request.execute();
        } else {
            return IQuery.dummy("PLAYER_GET_PROPERTY", CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(this.getUniqueId()).getProperty(name));
        }
    }

    @Override
    public IQuery<PropertyObject> getPropertySafely(String name) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            DriverRequest<PropertyObject> request = DriverRequest.create("PLAYER_GET_PROPERTY_SAFELY", PropertyObject.class);
            request.append("uniqueId", this.getUniqueId().toString());
            request.append("name", name);
            return request.execute();
        } else {
            return IQuery.dummy("PLAYER_GET_PROPERTY", CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(this.getUniqueId()).getProperty(name));
        }
    }

    @Override
    public NameChange[] getNameChanges() {
        return CloudDriver.getInstance().getMojangPool().getNameChanges(this.getUniqueId());
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
        return new CloudPlayerInventory(this);
    }

    @Override
    public void update() {
        CloudDriver.getInstance().getPlayerManager().update(this);

        PacketUpdatePlayer packetUpdatePlayer = new PacketUpdatePlayer(this);
        CloudDriver.getInstance().sendPacket(packetUpdatePlayer);

        DriverEventPlayerUpdate playerUpdate = new DriverEventPlayerUpdate(this);
        CloudDriver.getInstance().callEvent(playerUpdate);

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
    public void sendMessage(String message) {
        this.sendMessage(new ChatComponent(message));
    }

    @Override
    public IQuery<Boolean> sendActionbar(Object message) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_SEND_ACTION_BAR", "BUKKIT", Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        request.append("message", message.toString());
        return request.execute();
    }

    @Override
    public void sendMessage(ChatComponent chatComponent) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            if (CloudDriver.getInstance().getServiceManager().getCurrentService().getGroup().getType() == ServiceType.PROXY) {
                CloudDriver.getInstance().getProxyBridge().sendComponent(this.getUniqueId(), chatComponent);
                return;
            }
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendComponent(this.getUniqueId(), chatComponent));
    }

    @Override
    public IQuery<Boolean> openInventory(Inventory inventory) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_OPEN_INVENTORY", "BUKKIT", Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        request.append("inventory", inventory);
        return request.execute();
    }

    @Override
    public IQuery<Boolean> teleport(MinecraftLocation location) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_TELEPORT_LOCATION", "BUKKIT", Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        request.append("location", location);
        return request.execute();
    }

    @Override
    public IQuery<MinecraftLocation> getLocation() {
        DriverRequest<MinecraftLocation> request = DriverRequest.create("PLAYER_GET_LOCATION", "BUKKIT", MinecraftLocation.class);
        request.append("uniqueId", this.getUniqueId().toString());
        return request.execute();
    }

    @Override
    public IQuery<Boolean> sendTabList(ChatComponent header, ChatComponent footer) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            CloudDriver.getInstance().getBridgeInstance().sendTabList(this.getUniqueId(), header, footer);
            return IQuery.dummy("PLAYER_SEND_TABLIST", true);
        } else {
            DriverRequest<Boolean> request = DriverRequest.create("PLAYER_SEND_TABLIST", "PROXY", Boolean.class);
            request.append("uniqueId", this.getUniqueId().toString());
            request.append("header", header);
            request.append("footer", footer);
            return request.execute();
        }
    }

    @SneakyThrows @Override
    public IQuery<Boolean> playSound(Enum<?> sound, Float v1, Float v2) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_PLAY_SOUND", "BUKKIT", Boolean.class);

        request.append("uniqueId", this.getUniqueId().toString());
        request.append("sound", sound.name());
        request.append("v1", v1);
        request.append("v2", v2);
        return request.execute();
    }

    @Override
    public IQuery<Boolean> sendTitle(String title, String subtitle) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_SEND_TITLE", "BUKKIT", Boolean.class);

        request.append("uniqueId", this.getUniqueId().toString());
        request.append("title", title);
        request.append("subtitle", subtitle);
        return request.execute();
    }

    @Override
    public IQuery<Boolean> addProperty(String name, PropertyObject jsonObject) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            DriverRequest<Boolean> request = DriverRequest.create("PLAYER_ADD_PROPERTY", "CLOUD", Boolean.class);
            request.append("uniqueId", this.getUniqueId().toString());
            request.append("name", name);
            request.append("property", jsonObject);
            return request.execute();

        }
        offlinePlayer.addProperty(name, jsonObject);
        offlinePlayer.update();
        return IQuery.dummy("PLAYER_ADD_PROPERTY", true);
    }

    @Override
    public IQuery<Boolean> fallback() {

        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && CloudDriver.getInstance().getProxyBridge() != null) {
            CloudDriver.getInstance().getProxyBridge().fallbackPlayer(this.getUniqueId());
            return IQuery.dummy("PLAYER_FALLBACK", true);
        }

        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_FALLBACK", "PROXY", Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        return request.execute();
    }

    @Override
    public IQuery<Boolean> connect(IService service) {

        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && CloudDriver.getInstance().getProxyBridge() != null) {
            CloudDriver.getInstance().getProxyBridge().connectPlayer(this.getUniqueId(), service.getName());
            return IQuery.dummy("PLAYER_CONNECT_REQUEST", true);
        }

        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_CONNECT_REQUEST", this.getProxy().getName(), Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        request.append("server", service.getName());
        return request.execute();
    }

    @Override
    public IQuery<Boolean> connectRandom(IServiceGroup serviceGroup) {

        List<IService> services = CloudDriver.getInstance().getServiceManager().getCachedObjects(serviceGroup);
        IService service = services.get(new Random().nextInt(services.size()));

        return this.connect(service);
    }

    @Override
    public IQuery<Boolean> kick(String reason) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && CloudDriver.getInstance().getProxyBridge() != null) {
            CloudDriver.getInstance().getProxyBridge().kickPlayer(this.getUniqueId(), reason);
            return IQuery.dummy("PLAYER_KICK", true);
        }
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_KICK", "PROXY", Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        request.append("reason", reason);
        return request.execute();
    }

    @Override
    public ICloudPlayer sync() {
        return CloudDriver.getInstance().getPlayerManager().getCachedObject(this.getUniqueId());
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
    public Class<PlayerObject> getWrapperClass() {
        return PlayerObject.class;
    }

    @Override
    Class<ICloudPlayer> getInterface() {
        return ICloudPlayer.class;
    }
}