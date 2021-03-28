package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.ServerPinger;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.Getter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public class ServiceInfo extends Service {

    private final ServerPinger serverPinger;

    private String motd;
    private int maxPlayers;
    private boolean online;
    private final List<CloudPlayer> onlinePlayers;

    /**
     * Creates the ServiceInfo
     * @param name
     * @param uniqueId
     * @param serviceGroup
     * @param serviceID
     * @param port
     * @param cloudPort
     * @param serviceState
     * @param onlinePlayers
     */
    private ServiceInfo(String name, UUID uniqueId, ServiceGroup serviceGroup, int serviceID, int port, int cloudPort, ServiceState serviceState, List<CloudPlayer> onlinePlayers) {
        super(name, uniqueId, serviceGroup, serviceID, port, cloudPort, serviceState);
        this.serverPinger = new ServerPinger();
        this.onlinePlayers = onlinePlayers;
        try {
            this.onlinePlayers.removeIf(cloudPlayer -> !cloudPlayer.getConnectedService().getName().equalsIgnoreCase(name));
            try {
                this.serverPinger.pingServer(this.getHost(), port, 20);
                this.motd = this.serverPinger.getMotd();
                this.maxPlayers = this.serverPinger.getMaxplayers();
                this.online = this.serverPinger.isOnline();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            //THROWN ON BOOTUP IF Constants#CLOUDPLAYERS is null
        }
    }


    /**
     * Returns the {@link CloudPlayer}s on this
     * Service (for example "Lobby-1")
     *
     * @return List<CloudPlayer>
     */
    public List<CloudPlayer> getOnlinePlayers() {
        List<CloudPlayer> list = new LinkedList<>();
        for (CloudPlayer cloudPlayer : Constants.CLOUDPLAYERS.find(cloudPlayer -> cloudPlayer.getConnectedService().getName().equalsIgnoreCase(this.getName())).findAll()) {
            list.add(cloudPlayer);
        }
        return list;
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
