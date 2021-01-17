package de.lystx.cloudsystem.library.elements.packets.out.player;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
public class PacketPlayOutOnlinOnServices extends Packet implements Serializable {

    private final Map<ServiceGroup, Map<Service, List<CloudPlayer>>> cloudPlayers;

    public PacketPlayOutOnlinOnServices(Map<ServiceGroup, Map<Service, List<CloudPlayer>>> cloudPlayers) {
        super();
        this.cloudPlayers = cloudPlayers;
    }
}
