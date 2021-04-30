package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInServiceUpdate;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.CloudCache;
import de.lystx.cloudsystem.library.service.util.ServerPinger;
import io.vson.elements.object.Objectable;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class Service implements Serializable, Objectable<Service> {

    private final String name;
    private final UUID uniqueId;
    private final int serviceID;
    private final int port;
    private String host;
    private final int cloudPort;
    private final ServiceState serviceState;
    private SerializableDocument properties;
    private ServiceGroup serviceGroup;

    /**
     * Constructs service
     * @param name
     * @param uniqueId
     * @param serviceGroup
     * @param serviceID
     * @param port
     * @param cloudPort
     * @param serviceState
     */
    public Service(String name, UUID uniqueId, ServiceGroup serviceGroup, int serviceID, int port, int cloudPort, ServiceState serviceState) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.serviceGroup = serviceGroup;
        this.serviceID = serviceID;
        this.port = port;
        this.cloudPort = cloudPort;
        try {
            this.host = (InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            this.host = ("0");
        }
        this.serviceState = serviceState;
    }

    /**
     * Checks if Service is for example
     * SPIGOT or PROXY
     *
     * @param serviceType
     * @return
     */
    public boolean isInstanceOf(ServiceType serviceType) {
        return this.serviceGroup.getServiceType().equals(serviceType);
    }

    /**
     * Returns the {@link CloudPlayer}s on this
     * Service (for example "Lobby-1")
     *
     * @return List<CloudPlayer>
     */
    public List<CloudPlayer> getOnlinePlayers() {
        List<CloudPlayer> list = new LinkedList<>();
        for (CloudPlayer globalOnlinePlayer : CloudCache.CLOUDPLAYERS) {
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
        CloudCache.getInstance().getCurrentCloudExecutor().sendPacket(new PacketInServiceUpdate(this));
    }

    @SneakyThrows
    /**
     * This pings the current Service
     * to get all Data of it
     */
    private ServerPinger ping() {
        ServerPinger serverPinger = new ServerPinger();
        serverPinger.pingServer(this.host, this.port, 20);
        return serverPinger;
    }

    @Override
    public String toString() {
        return name;
    }
}
