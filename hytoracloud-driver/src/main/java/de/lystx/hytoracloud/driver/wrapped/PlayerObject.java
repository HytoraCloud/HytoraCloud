package de.lystx.hytoracloud.driver.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionEntry;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.player.featured.IPlayerSettings;
import de.lystx.hytoracloud.driver.player.required.IPlayerConnection;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.player.required.OfflinePlayer;
import de.lystx.hytoracloud.driver.player.inventory.Inventory;
import de.lystx.hytoracloud.driver.service.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.event.events.player.other.DriverEventPlayerUpdate;
import de.lystx.hytoracloud.driver.service.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.player.*;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.utils.other.NameChange;
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
    private IPlayerConnection connection;

    /**
     * The information of the player
     */
    private OfflinePlayer offlinePlayer;

    public PlayerObject(PlayerConnectionObject connection) {
        this(connection, "Bungee-1"); //Just a dummy proxy
    }

    public PlayerObject(IPlayerConnection connection, String proxy) {
        this.connection = connection;
        this.proxy = proxy;
        this.setOfflinePlayer(CloudDriver.getInstance().getPermissionPool().getCachedObject(connection.getUniqueId()));
    }

    @Override
    public IService getService() {
        return CloudDriver.getInstance().getServiceManager().getCachedObject(this.service);
    }

    @Override
    public String getServiceAsString() {
        return this.service;
    }

    @Override
    public IService getProxy() {
        return CloudDriver.getInstance().getServiceManager().getCachedObject(this.proxy);
    }

    @Override
    public Optional<IService> getOptionalProxy() {
        return CloudDriver.getInstance().getServiceManager().getOptional(this.proxy);
    }

    @Override
    public Optional<IService> getOptionalService() {
        return  CloudDriver.getInstance().getServiceManager().getOptional(this.service);
    }

    @Override
    public String getProxyAsString() {
        return this.proxy;
    }

    public void setService(IService service) {
        this.service = service == null ? "No Server" : service.getName();
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
    public String getIpAddress() {
        return this.connection.getHost();
    }

    @Override
    public DriverQuery<Integer> getPing() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            return DriverQuery.dummy("PLAYER_GET_PING", CloudDriver.getInstance().getBridgeInstance().getPing(this.getUniqueId()));
        }
        DriverRequest<Integer> request = DriverRequest.create("PLAYER_GET_PING", Integer.class);
        request.append("uniqueId", this.getUniqueId().toString());
        return request.execute();
    }

    @Override
    public DriverQuery<PermissionGroup> getPermissionGroup() {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.BRIDGE)) {

            DriverRequest<PermissionGroup> request = DriverRequest.create("PLAYER_GET_PERMISSIONGROUP", "CLOUD", PermissionGroup.class);
            request.append("uniqueId", this.getUniqueId().toString());
            return request.execute();
        } else {
            return DriverQuery.dummy("PLAYER_GET_PERMISSIONGROUP", CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId()));
        }
    }

    @Override
    public DriverQuery<PropertyObject> getProperty(String name) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            DriverRequest<PropertyObject> request = DriverRequest.create("PLAYER_GET_PROPERTY", PropertyObject.class);
            request.append("uniqueId", this.getUniqueId().toString());
            request.append("name", name);
            return request.execute();
        } else {
            return DriverQuery.dummy("PLAYER_GET_PROPERTY", CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(this.getUniqueId()).getProperty(name));
        }
    }

    @Override
    public DriverQuery<Boolean> updateProperty(String name, PropertyObject jsonObject) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            DriverRequest<Boolean> request = DriverRequest.create("PLAYER_ADD_PROPERTY", "CLOUD", Boolean.class);
            request.append("uniqueId", this.getUniqueId().toString());
            request.append("name", name);
            request.append("property", jsonObject);
            return request.execute();
        }
        offlinePlayer.addProperty(name, jsonObject);
        offlinePlayer.update();
        return DriverQuery.dummy("PLAYER_ADD_PROPERTY", true);
    }

    @Override
    public DriverQuery<PropertyObject> getPropertySafely(String name) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            DriverRequest<PropertyObject> request = DriverRequest.create("PLAYER_GET_PROPERTY_SAFELY", "CLOUD", PropertyObject.class);
            request.append("uniqueId", this.getUniqueId().toString());
            request.append("name", name);
            return request.execute();
        } else {
            return DriverQuery.dummy("PLAYER_GET_PROPERTY", CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(this.getUniqueId()).getProperty(name));
        }
    }

    @Override
    public IPlayerSettings getSettings() {
        DriverRequest<IPlayerSettings> request = DriverRequest.create("PLAYER_GET_SETTINGS", this.getProxyAsString(), IPlayerSettings.class);
        request.append("uniqueId", this.getUniqueId().toString());
        return request.execute().setTimeOut(60, null).pullValue();
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
    public void update() {
        CloudDriver.getInstance().getPlayerManager().update(this);

        PacketUpdatePlayer packetUpdatePlayer = new PacketUpdatePlayer(this);
        CloudDriver.getInstance().sendPacket(packetUpdatePlayer);

        DriverEventPlayerUpdate playerUpdate = new DriverEventPlayerUpdate(this);
        CloudDriver.getInstance().getEventManager().callEvent(playerUpdate);

    }


    @Override @SneakyThrows
    public DriverQuery<Boolean> addPermission(String permission) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM && this.offlinePlayer != null) {
            return this.offlinePlayer.addPermission(permission);
        }
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_ADD_PERMISSION", "CLOUD", Boolean.class);
        request.append("uniqueId", this.getUniqueId());
        request.append("permission", permission);
        return request.execute();
    }

    @Override
    public DriverQuery<Boolean> removePermission(String permission) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM && this.offlinePlayer != null) {
            return this.offlinePlayer.removePermission(permission);
        }
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_REMOVE_PERMISSION", "CLOUD", Boolean.class);
        request.append("uniqueId", this.getUniqueId());
        request.append("permission", permission);
        return request.execute();
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
    public DriverQuery<PermissionGroup> removePermissionGroup(PermissionGroup permissionGroup) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            CloudDriver.getInstance().getPermissionPool().removePermissionGroupFromUser(this.getUniqueId(), permissionGroup);
            CloudDriver.getInstance().getPermissionPool().update();
            return DriverQuery.dummy("PLAYER_ADD_GROUP", permissionGroup);
        } else {
            DriverRequest<PermissionGroup> request = DriverRequest.create("PLAYER_ADD_GROUP", "CLOUD", PermissionGroup.class);
            request.append("uniqueId", this.getUniqueId());
            request.append("group", permissionGroup.getName());
            return request.execute();
        }
    }

    @Override
    public DriverQuery<PermissionGroup> addPermissionGroup(PermissionGroup permissionGroup, int time, PermissionValidity unit) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            CloudDriver.getInstance().getPermissionPool().addPermissionGroupToUser(this.getUniqueId(), permissionGroup, time, unit);
            CloudDriver.getInstance().getPermissionPool().update();
            return DriverQuery.dummy("PLAYER_ADD_GROUP", permissionGroup);
        } else {
            DriverRequest<PermissionGroup> request = DriverRequest.create("PLAYER_ADD_GROUP", "CLOUD", PermissionGroup.class);
            request.append("uniqueId", this.getUniqueId());
            request.append("group", permissionGroup.getName());
            request.append("time", time);
            request.append("unit", unit.name());
            return request.execute();
        }
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage(new ChatComponent(message));
    }

    @Override
    public DriverQuery<Boolean> sendActionbar(Object message) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_SEND_ACTION_BAR", "BUKKIT", Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        request.append("message", message.toString());
        return request.execute();
    }

    @Override
    public void sendMessage(ChatComponent chatComponent) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            CloudDriver.getInstance().getBridgeInstance().sendMessage(this.getUniqueId(), chatComponent);
            return;
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendComponent(this.getUniqueId(), chatComponent));
    }

    @Override
    public DriverQuery<Boolean> openInventory(Inventory inventory) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_OPEN_INVENTORY", "BUKKIT", Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        request.append("inventory", inventory);
        return request.execute();
    }

    @Override
    public DriverQuery<Boolean> teleport(MinecraftLocation location) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_TELEPORT_LOCATION", "BUKKIT", Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        request.append("location", location);
        return request.execute();
    }

    @Override
    public DriverQuery<MinecraftLocation> getLocation() {
        DriverRequest<MinecraftLocation> request = DriverRequest.create("PLAYER_GET_LOCATION", "BUKKIT", MinecraftLocation.class);
        request.append("uniqueId", this.getUniqueId().toString());
        return request.execute();
    }

    @Override
    public DriverQuery<Boolean> sendTabList(String header, String footer) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_SEND_TABLIST", "PROXY", Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        request.append("header", header);
        request.append("footer", footer);
        return request.execute();
    }

    @SneakyThrows @Override
    public DriverQuery<Boolean> playSound(Enum<?> sound, Float v1, Float v2) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_PLAY_SOUND", "BUKKIT", Boolean.class);

        request.append("uniqueId", this.getUniqueId().toString());
        request.append("sound", sound.name());
        request.append("v1", v1);
        request.append("v2", v2);
        return request.execute();
    }

    @Override
    public DriverQuery<Boolean> sendTitle(String title, String subtitle) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_SEND_TITLE", "BUKKIT", Boolean.class);

        request.append("uniqueId", this.getUniqueId().toString());
        request.append("title", title);
        request.append("subtitle", subtitle);
        return request.execute();
    }

    @Override
    public DriverQuery<Boolean> fallback() {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_FALLBACK", "PROXY", Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        return request.execute();
    }

    @Override
    public DriverQuery<Boolean> connect(IService service) {
        DriverRequest<Boolean> request = DriverRequest.create("PLAYER_CONNECT_REQUEST", this.getProxy().getName(), Boolean.class);
        request.append("uniqueId", this.getUniqueId().toString());
        request.append("server", service.getName());
        return request.execute();
    }

    @Override
    public DriverQuery<Boolean> connectRandom(IServiceGroup serviceGroup) {

        List<IService> services = CloudDriver.getInstance().getServiceManager().getCachedObjects(serviceGroup);
        IService service = services.get(new Random().nextInt(services.size()));

        return this.connect(service);
    }

    @Override
    public DriverQuery<Boolean> kick(String reason) {
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
    public boolean isOnline() {
        return CloudDriver.getInstance().getPlayerManager().getCachedObject(this.getUniqueId()) != null;
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