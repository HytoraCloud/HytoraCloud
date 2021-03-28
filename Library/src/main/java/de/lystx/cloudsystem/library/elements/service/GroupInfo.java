package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public class GroupInfo extends ServiceGroup {

    private final List<Service> onlineServices;

    private GroupInfo(UUID uniqueId, String name, String receiver, String template, ServiceType serviceType, int maxServer, int minServer, int maxRam, int minRam, int maxPlayers, int newServerPercent, boolean maintenance, boolean lobby, boolean dynamic, List<CloudPlayer> onlinePlayers, List<Service> onlineServices) {
        super(uniqueId, name, template, serviceType, receiver, maxServer, minServer, maxRam, minRam, maxPlayers, newServerPercent, maintenance, lobby, dynamic);
        this.onlineServices = new LinkedList<>(onlineServices);

        this.onlineServices.removeIf(onlineService -> !onlineService.getServiceGroup().getName().equalsIgnoreCase(name));
    }

    /**
     * Returns the {@link CloudPlayer}s on this
     * ServiceGroup (for example "Lobby")
     *
     * @return List with CloudPlayers on this Group
     */
    public List<CloudPlayer> getOnlinePlayers() {
        List<CloudPlayer> list = new LinkedList<>();
        for (CloudPlayer cloudPlayer : Constants.CLOUDPLAYERS.find(cloudPlayer -> cloudPlayer.getConnectedService().getServiceGroup().getName().equalsIgnoreCase(this.getName())).findAll()) {
            list.add(cloudPlayer);
        }
        return list;
    }


    /**
     * Returns groupInfo
     * @param serviceGroup
     * @param cloudPlayers
     * @param services
     * @return
     */
    public static GroupInfo fromGroup(ServiceGroup serviceGroup, List<CloudPlayer> cloudPlayers, List<Service> services) {
        return new GroupInfo(serviceGroup.getUniqueId(), serviceGroup.getName(), serviceGroup.getReceiver(),  serviceGroup.getTemplate(), serviceGroup.getServiceType(), serviceGroup.getMaxServer(), serviceGroup.getMinServer(), serviceGroup.getMaxRam(), serviceGroup.getMinRam(), serviceGroup.getMaxPlayers(), serviceGroup.getNewServerPercent(), serviceGroup.isMaintenance(), serviceGroup.isLobby(), serviceGroup.isDynamic(), new LinkedList<>(cloudPlayers), services);
    }
}
