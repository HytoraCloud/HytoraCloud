package de.lystx.hytoracloud.driver.commons.implementations;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestTPS;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.utils.minecraft.ServerPinger;
import de.lystx.hytoracloud.driver.utils.minecraft.ServiceInfo;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class ServiceObject implements IService {

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
    private int id;

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
    private ServiceState state;

    /**
     * The properties of this service to store values
     */
    private PropertyObject properties;

    /**
     * The group of this service
     */
    private IServiceGroup group;

    /**
     * If the service is connected to the cloud
     */
    private boolean authenticated;

    public ServiceObject(IServiceGroup group, int id, int port) {
        this(group.getName() + "-" + id, UUID.randomUUID(), id, port, CloudDriver.getInstance().getHost().getAddress().getHostAddress(), ServiceState.LOBBY, new PropertyObject(), group, false);
    }

    /**
     * Adds a property to this service
     *
     * @param key the name of the property
     * @param data the data
     */
    public void addProperty(String key, PropertyObject data) {
        this.properties.append(key, data);
    }

    /**
     * Checks if Service is for example
     * SPIGOT or PROXY
     *
     * @param serviceType the type to compare with
     * @return boolean
     */
    public boolean isInstanceOf(ServiceType serviceType) {
        return this.group.getType().equals(serviceType);
    }

    /**
     * Returns the {@link CloudPlayer}s on this
     * Service (for example "Lobby-1")
     *
     * @return List of cloudPlayers on this service
     */
    public List<CloudPlayer> getPlayers() {
        List<CloudPlayer> list = new LinkedList<>();
        for (CloudPlayer globalOnlinePlayer : CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers()) {
            if (globalOnlinePlayer == null || globalOnlinePlayer.getService() == null) {
                continue;
            }
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
        if (group.getType().equals(ServiceType.PROXY)) {
            throw new UnsupportedOperationException("Not available for Proxy!");
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
        if (group.getType().equals(ServiceType.PROXY)) {
            throw new UnsupportedOperationException("Not available for Proxy!");
        }
        return this.ping().getMaxplayers();
    }

    /**
     * Gets the formatted tps of this
     * minecraft server
     *
     * @return tps in string with color
     */
    public String getTPS() {
        PacketRequestTPS packetRequestTPS = new PacketRequestTPS(this.name);

        Component component = packetRequestTPS.toReply(CloudDriver.getInstance().getConnection(), 3000);
        String message = component.reply().getMessage();

        return message.equalsIgnoreCase("The request timed out") ? "§c???" : message;
    }

    /**
     * Updates this Service
     * and syncs it all over the cloud
     */
    public void update() {
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


    /**
     * Prepares a {@link ServiceInfo}
     * to get values like motd and so on
     *
     * @return prepared info
     */
    public ServiceInfo prepare() {
        return ServiceInfo.prepare(this.host, this.port);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Copies this service 1:1
     * @return copied service
     */
    public IService deepCopy() {
        IService service = new ServiceObject(this.group, this.id, this.port);
        service.setId(this.id);
        service.setUniqueId(this.uniqueId);
        service.setState(this.state);
        service.setHost(this.host);
        service.setGroup(this.group);
        service.setPort(this.port);
        service.setProperties(this.properties);
        service.setAuthenticated(this.authenticated);
        return service;
    }

}
