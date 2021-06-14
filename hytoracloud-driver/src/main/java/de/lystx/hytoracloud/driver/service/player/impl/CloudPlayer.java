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
import de.lystx.hytoracloud.driver.service.player.featured.labymod.LabyModPlayer;
import de.lystx.hytoracloud.driver.service.util.reflection.Reflections;
import de.lystx.hytoracloud.driver.service.uuid.NameChange;
import de.lystx.hytoracloud.driver.service.uuid.UUIDService;
import io.thunder.packet.PacketBuffer;
import io.thunder.packet.impl.response.IResponse;
import io.thunder.packet.impl.response.Response;
import io.thunder.packet.impl.response.ResponseStatus;
import io.thunder.utils.objects.ThunderObject;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TODO: DOCUMENTATION (AGAIN)
@Getter @Setter
public class CloudPlayer implements Serializable, CloudCommandSender, ThunderObject, IPermissionUser, Identifiable {

    public static Class<? extends CloudPlayer> TYPE_CLASS = CloudPlayer.class;

    private String service;
    private String proxy;

    private PlayerConnection connection;

    private PlayerInformation playerInformation;
    private LabyModPlayer labyModPlayer;

    public CloudPlayer(PlayerConnection connection) {
        this.connection = connection;
        this.setPlayerInformation(CloudDriver.getInstance().getPermissionPool().getPlayerInformation(connection.getUniqueId()));
    }

    public Service getService() {
        return CloudDriver.getInstance().getServiceManager().getService(this.service);
    }

    
    public void setService(Service service) {
        this.service = service == null ? "No Server" : service.getName();
    }

    public Service getProxy() {
        return CloudDriver.getInstance().getServiceManager().getService(this.proxy);
    }

    
    public void setProxy(Service service) {
        this.proxy = service.getName();
    }

    
    public void setPlayerInformation() {
        this.setPlayerInformation(CloudDriver.getInstance().getPermissionPool().getPlayerInformation(this.getUniqueId()));
    }

    public String getName() {
        return this.connection.getName();
    }

    public UUID getUniqueId() {
        return this.connection.getUniqueId();
    }

    
    public void setUniqueId(UUID uniqueId) {
        throw new UnsupportedOperationException("Not available for CloudPlayer");
    }

    
    public void setName(String name) {
        throw new UnsupportedOperationException("Not available for CloudPlayer");
    }

    public String getIpAddress() {
        return this.connection.getAddress();
    }

    
    public IResponse<MinecraftWorld> getWorld() {
        PacketRequestPlayerWorld packetRequestPlayerWorld = new PacketRequestPlayerWorld(this.getUniqueId());
        Response response = CloudDriver.getInstance().getResponse(packetRequestPlayerWorld);
        return response.toIResponse(new MinecraftWorld(response.get(0).asString(), response.get(1).asUUID()));
    }

    
    public IResponse<MinecraftLocation> getLocation() {
        PacketRequestPlayerLocation playerLocation = new PacketRequestPlayerLocation(this.getUniqueId());
        Response response = CloudDriver.getInstance().getResponse(playerLocation);
        return response.toIResponse(new MinecraftLocation(response.get(0).asCustom(Double.class), response.get(1).asCustom(Double.class), response.get(2).asCustom(Double.class), response.get(3).asCustom(Float.class), response.get(4).asCustom(Float.class), response.get(5).asString()));
    }

    public NameChange[] getNameChanges() {
        return UUIDService.getInstance().getNameChanges(this.getUniqueId());
    }

    public boolean hasPlayedBefore() {
        return this.playerInformation.getFirstLogin() == 0L || this.playerInformation.getFirstLogin() == System.currentTimeMillis();
    }

    public PlayerInformation getPlayerInformation() {
        return this.playerInformation == null ? CloudDriver.getInstance().getPermissionPool().getDefaultPlayerInformation(this.getUniqueId(), this.getName(), this.getIpAddress()) : this.playerInformation;
    }

    /**
     * Returns the ping of the player
     * @return
     */
    public IResponse<Integer> getPing() {
        Response response = CloudDriver.getInstance().getConnection().transferToResponse(new PacketRequestPing(this.getUniqueId()));
        return response.toIResponse(response.get(0).asInt());
    }

    /**
     * Returns a CloudPlayer Inventory to manage stuff
     * @return
     */
    public CloudPlayerInventory getInventory() {
        return CloudDriver.getInstance().getCloudInventories().getOrDefault(this.getUniqueId(), new CloudPlayerInventory(this));
    }

    /**
     * Updates a player and all his data
     */
    public void update() {
        if (this.playerInformation != null) {
            this.playerInformation.update();
        }
        CloudDriver.getInstance().sendPacket(new PacketUpdatePlayer(this));
    }

    
    public IResponse<PermissionGroup> getPermissionGroup() {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.BRIDGE)) {
            PacketRequestPermissionGroupGet groupGet = new PacketRequestPermissionGroupGet(this.getUniqueId());
            Response response = CloudDriver.getInstance().getResponse(groupGet);
            return response.toIResponse(response.get(0).asCustom(PermissionGroup.class));
        } else {
            return new Response(ResponseStatus.SUCCESS).toIResponse(CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId()));
        }
    }

    
    public PermissionGroup getHighestPermissionGroup() {
        return CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId());
    }

    
    public void addPermission(String permission) {
        if (this.playerInformation != null) {
            this.playerInformation.getExclusivePermissions().add(permission);
        }
    }

    
    public void removePermission(String permission) {
        if (this.playerInformation != null) {
            this.playerInformation.getExclusivePermissions().remove(permission);
        }
    }

    
    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        CloudDriver.getInstance().getPermissionPool().updatePermissions(this.getUniqueId(), this.getIpAddress(), permissions::add);
        return permissions;
    }

    
    public List<String> getExclusivePermissions() {
        return this.playerInformation.getPermissions();
    }

    
    public List<PermissionGroup> getAllPermissionGroups() {
        return CloudDriver.getInstance().getPermissionPool().getCachedPermissionGroups(this.getUniqueId());
    }

    
    public void removePermissionGroup(PermissionGroup permissionGroup) {
        CloudDriver.getInstance().getPermissionPool().removePermissionGroupFromUser(this.getUniqueId(), permissionGroup);
        CloudDriver.getInstance().getPermissionPool().update();
    }

    
    public void addPermissionGroup(PermissionGroup permissionGroup, int time, PermissionValidity unit) {
        CloudDriver.getInstance().getPermissionPool().addPermissionGroupToUser(this.getUniqueId(), permissionGroup, time, unit);
        CloudDriver.getInstance().getPermissionPool().update();
    }

    public void sendMessage(Object message) {
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.BRIDGE)) {
            Object player;
            if (CloudDriver.getInstance().getThisService().getServiceGroup().getServiceType() == ServiceType.SPIGOT) {
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
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendMessage(this.getUniqueId(), message.toString()));
    }

    public void sendActionbar(Object message) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendActionbar(this.getUniqueId(), message.toString()));
    }

    public void sendComponent(CloudComponent cloudComponent) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendComponent(this.getUniqueId(), cloudComponent));
    }

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

    public void sendTitle(String title, String subtitle) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketSendTitle(this.getName(), title, subtitle));
    }

    
    public ResponseStatus addProperty(String name, JsonObject jsonObject) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            return CloudDriver.getInstance().getResponse(new PacketRequestAddProperty(this.getUniqueId(), name, jsonObject)).getStatus();
        }
        playerInformation.addProperty(name, jsonObject);
        playerInformation.update();
        return ResponseStatus.SUCCESS;
    }

    
    public IResponse<JsonObject> getProperty(String name) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            Response response = CloudDriver.getInstance().getResponse(new PacketRequestGetProperty(this.getUniqueId(), name));
            return response.toIResponse((JsonObject) new JsonParser().parse(response.getMessage()));
        } else {
            Response response = new Response(ResponseStatus.SUCCESS);

            return response.toIResponse(CloudDriver.getInstance().getCloudPlayerManager().getOfflinePlayer(this.getUniqueId()).getProperty(name));
        }
    }

    public void fallback() {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketFallback(this.getName()));
    }

    public void connect(Service service) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketConnectServer(this.getName(), service.getName()));
    }

    public void connectRandom(ServiceGroup serviceGroup) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketConnectGroup(this.getName(), serviceGroup.getName()));
    }

    public void kick(String reason) {
        this.getConnection().disconnect(reason);
    }

    public boolean hasPermission(String permission) {
        return CloudDriver.getInstance().getPermissionPool().hasPermission(this.getUniqueId(), permission);
    }

    @Nullable
    
    public PermissionGroup getCachedPermissionGroup() {
        return CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(this.getUniqueId());
    }

    public void sendMessage(String prefix, String message) {
        throw new UnsupportedOperationException("Only works on CloudConsole!");
    }

    
    public void write(PacketBuffer buf) {

        buf.writeThunderObject(getConnection()); //Name, UUID, iP
        buf.nullSafe().writeThunderObject(getPlayerInformation());

        buf.writeString(this.getProxy() != null ? this.getProxy().getName() : "null");
        buf.writeString(this.getService() != null ? this.getService().getName() : "null");
        //TODO: LABYMODPLAYER SERIALIZE IN BUF
    }



    public void read(PacketBuffer buf) {

        setConnection(buf.readThunderObject(PlayerConnection.class));
        setPlayerInformation(buf.nullSafe().readThunderObject(PlayerInformation.class));

        String proxy = buf.readString();
        String service = buf.readString();

        setProxy(proxy.equalsIgnoreCase("null") ? null : CloudDriver.getInstance().getServiceManager().getService(proxy));
        setService(service.equalsIgnoreCase("null") ? null : CloudDriver.getInstance().getServiceManager().getService(service));
    }
}