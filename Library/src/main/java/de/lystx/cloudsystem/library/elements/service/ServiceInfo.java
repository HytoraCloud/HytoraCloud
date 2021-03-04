package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.serverselector.sign.manager.ServerPinger;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Getter
public class ServiceInfo extends Service{

    private final ServerPinger serverPinger;

    private String motd;
    private int maxPlayers;
    private boolean online;
    private final List<CloudPlayer> onlinePlayers;

    private ServiceInfo(String name, UUID uniqueId, ServiceGroup serviceGroup, int serviceID, int port, int cloudPort, ServiceState serviceState, List<CloudPlayer> onlinePlayers) {
        super(name, uniqueId, serviceGroup, serviceID, port, cloudPort, serviceState);
        this.serverPinger = new ServerPinger();
        this.onlinePlayers = onlinePlayers;
        this.onlinePlayers.removeIf(cloudPlayer -> !cloudPlayer.getServer().equalsIgnoreCase(name));
        try {
            this.serverPinger.pingServer(this.getHost(), port, 20);
            this.motd = this.serverPinger.getMotd();
            this.maxPlayers = this.serverPinger.getMaxplayers();
            this.online = this.serverPinger.isOnline();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs ServiceInfo
     * @param service
     * @param cloudPlayers
     * @return
     */
    public static ServiceInfo fromService(Service service, List<CloudPlayer> cloudPlayers) {
        return new ServiceInfo(service.getName(), service.getUniqueId(), service.getServiceGroup(), service.getServiceID(), service.getPort(), service.getCloudPort(), service.getServiceState(), cloudPlayers);
    }
}
