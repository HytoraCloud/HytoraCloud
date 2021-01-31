package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class GroupInfo extends ServiceGroup {

    private final List<CloudPlayer> onlinePlayers;
    private final List<Service> onlineServices;

    private GroupInfo(UUID uniqueId, String name, String template, ServiceType serviceType, int maxServer, int minServer, int maxRam, int minRam, int maxPlayers, int newServerPercent, boolean maintenance, boolean lobby, boolean dynamic, List<CloudPlayer> onlinePlayers, List<Service> onlineServices) {
        super(uniqueId, name, template, serviceType, maxServer, minServer, maxRam, minRam, maxPlayers, newServerPercent, maintenance, lobby, dynamic);
        this.onlinePlayers = onlinePlayers;
        this.onlineServices = onlineServices;

        this.onlinePlayers.removeIf(onlinePlayer -> !onlinePlayer.getGroup().equalsIgnoreCase(name));
        this.onlineServices.removeIf(onlineService -> !onlineService.getServiceGroup().getName().equalsIgnoreCase(name));
    }

    public static GroupInfo fromGroup(ServiceGroup serviceGroup, List<CloudPlayer> cloudPlayers, List<Service> services) {
        return new GroupInfo(serviceGroup.getUniqueId(), serviceGroup.getName(), serviceGroup.getTemplate(), serviceGroup.getServiceType(), serviceGroup.getMaxServer(), serviceGroup.getMinServer(), serviceGroup.getMaxRam(), serviceGroup.getMinRam(), serviceGroup.getMaxPlayers(), serviceGroup.getNewServerPercent(), serviceGroup.isMaintenance(), serviceGroup.isLobby(), serviceGroup.isDynamic(), cloudPlayers, services);
    }
}
