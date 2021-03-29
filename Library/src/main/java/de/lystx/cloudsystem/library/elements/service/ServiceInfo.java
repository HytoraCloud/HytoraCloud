package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.elements.list.CloudList;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.ServerPinger;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public class ServiceInfo extends Service {

    private final ServerPinger serverPinger;

    private String motd;
    private int maxPlayers;
    private boolean online;
    private int onlinePlayers;

    /**
     * Creates the ServiceInfo
     * @param name
     * @param uniqueId
     * @param serviceGroup
     * @param serviceID
     * @param port
     * @param cloudPort
     * @param serviceState
     */
    private ServiceInfo(String name, UUID uniqueId, ServiceGroup serviceGroup, int serviceID, int port, int cloudPort, ServiceState serviceState) {
        super(name, uniqueId, serviceGroup, serviceID, port, cloudPort, serviceState);
        this.serverPinger = new ServerPinger();
        try {
            this.serverPinger.pingServer(this.getHost(), port, 20);
            this.motd = this.serverPinger.getMotd();
            this.onlinePlayers = this.serverPinger.getPlayers();
            this.maxPlayers = this.serverPinger.getMaxplayers();
            this.online = this.serverPinger.isOnline();
        } catch (Exception e) {
            //SERVICEINFO BROKEN /COULD NOT GET MOTD OR MAXPLAYERS OR ONLINEPLAYERS
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
        for (CloudPlayer globalOnlinePlayer : Constants.CLOUDPLAYERS) {
            if (!globalOnlinePlayer.getServer().equalsIgnoreCase(this.getName())) {
                continue;
            }
            list.add(globalOnlinePlayer);
        }
        return list;
    }


    /**
     * Constructs ServiceInfo
     * @param service
     * @return
     */
    public static ServiceInfo fromService(Service service) {
        return new ServiceInfo(service.getName(), service.getUniqueId(), service.getServiceGroup(), service.getServiceID(), service.getPort(), service.getCloudPort(), service.getServiceState());
    }
}
