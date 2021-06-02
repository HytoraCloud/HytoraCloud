package de.lystx.hytoracloud.driver.elements.service;

import com.google.gson.JsonObject;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.packets.both.service.ServicePacket;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.enums.ServiceState;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.util.minecraft.ServerPinger;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class Service implements Serializable, Objectable<Service> {

    /**
     * The name of this service
     */
    private String name;

    /**
     * The uuid of this service
     */
    private UUID uniqueId;

    /**
     * The ID of this service
     */
    private int serviceID;

    /**
     * The port of this service
     */
    private int port;

    /**
     * The host of the cloud to connect to
     */
    private String host;

    /**
     * The state of this service
     */
    private ServiceState serviceState;

    /**
     * The properties of this service to store values
     */
    private JsonObject properties;

    /**
     * The group of this service
     */
    private ServiceGroup serviceGroup;

    /**
     * If the service is connected to the cloud
     */
    private boolean authenticated;

    public Service(ServiceGroup serviceGroup, int id, int port) {
        this(serviceGroup.getName() + "-" + id, UUID.randomUUID(), id, port, CloudDriver.getInstance().getHost().getAddress().getHostAddress(), ServiceState.LOBBY, new JsonObject(), serviceGroup, false);
    }

    /**
     * Adds a property to this service
     *
     * @param key the name of the property
     * @param data the data
     */
    public void addProperty(String key, JsonObject data) {
        this.properties.add(key, data);
    }

    /**
     * Checks if Service is for example
     * SPIGOT or PROXY
     *
     * @param serviceType the type to compare with
     * @return boolean
     */
    public boolean isInstanceOf(ServiceType serviceType) {
        return this.serviceGroup.getServiceType().equals(serviceType);
    }

    /**
     * Returns the {@link CloudPlayer}s on this
     * Service (for example "Lobby-1")
     *
     * @return List of cloudPlayers on this service
     */
    public List<CloudPlayer> getOnlinePlayers() {
        List<CloudPlayer> list = new LinkedList<>();
        for (CloudPlayer globalOnlinePlayer : CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers()) {
            if (!globalOnlinePlayer.getService().getName().equalsIgnoreCase(this.getName())) {
                continue;
            }
            list.add(globalOnlinePlayer);
        }
        return list;
    }

    /**
     * Returns the Motd of this Service
     * might lag if the Service has not been
     * pinged before
     *
     * @return Motd of service
     */
    public String getMotd() {
        if (serviceGroup.getServiceType().equals(ServiceType.PROXY)) {
            return "Not available for proxy";
        }
        return this.ping().getMotd();
    }

    /**
     * Returns the Maximum PLayers of this Service
     * might lag if the Service has not been
     * pinged before
     *
     * @return Maximum PLayers of service
     */
    public int getMaxPlayers() {
        if (serviceGroup.getServiceType().equals(ServiceType.PROXY)) {
            throw new UnsupportedOperationException("Not available for Proxy!");
        }
        return this.ping().getMaxplayers();
    }

    /**
     * Updates this Service
     * and syncs it all over the cloud
     */
    public void update() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDAPI) {
            JsonBuilder jsonBuilder = new JsonBuilder(new File("./CLOUD/connection.json"));
            jsonBuilder.append(this);
            jsonBuilder.save();
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketServiceUpdate(this));
    }

    /**
     * Stops this service
     */
    public void shutdown() {
        CloudDriver.getInstance().getServiceManager().stopService(this);
    }

    /**
     * This pings the current Service
     * to get all Data of it
     */
    private ServerPinger ping() {
        ServerPinger serverPinger = new ServerPinger();
        if (this.host == null) {
            return serverPinger;
        }
        try {
            serverPinger.pingServer(this.host, this.port, 20);
        } catch (Exception e) {
            try {
                serverPinger.pingServer(this.host, this.port, 200);
            } catch (IOException ioException) {
                //Ignoring
            }
        };
        return serverPinger;
    }

    @Override
    public String toString() {
        return name;
    }

    @SneakyThrows
    public void writeToBuf(PacketBuffer buf) {
        buf.writeString(name);
        buf.writeUUID(uniqueId);
        buf.writeInt(serviceID);
        buf.writeInt(port);
        buf.writeString(host);
        buf.writeEnum(serviceState);
        buf.writeString(properties.toString());
        serviceGroup.writeToBuf(buf);
        buf.writeBoolean(authenticated);

    }

    public static Service readFromBuf(PacketBuffer buf) {

        String name = buf.readString();
        UUID uniqueId = buf.readUUID();
        int id = buf.readInt();
        int port = buf.readInt();
        String host = buf.readString();
        ServiceState state = buf.readEnum(ServiceState.class);
        JsonObject properties = new JsonBuilder(buf.readString()).build();
        ServiceGroup serviceGroup = ServiceGroup.readFromBuf(buf);
        boolean authenticated = buf.readBoolean();

        return new Service(name, uniqueId, id, port, host, state, properties, serviceGroup, authenticated);

    }

    /**
     * Copies this service 1:1
     * @return
     */
    public Service deepCopy() {
        Service service = new Service(this.serviceGroup, this.serviceID, this.port);
        service.setServiceID(this.serviceID);
        service.setUniqueId(this.uniqueId);
        service.setServiceState(this.serviceState);
        service.setHost(this.host);
        service.setServiceGroup(this.serviceGroup);
        service.setPort(this.port);
        service.setProperties(this.properties);
        service.setAuthenticated(this.authenticated);
        return service;
    }

    /**
     * Sends a {@link Packet} to only this service
     *
     * @param packet the packet to send
     */
    public void sendPacket(Packet packet) {
        ServicePacket servicePacket = new ServicePacket(this.name, packet);
        CloudDriver.getInstance().sendPacket(servicePacket);
    }

}
