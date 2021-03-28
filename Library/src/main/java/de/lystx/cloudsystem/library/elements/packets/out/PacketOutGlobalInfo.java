package de.lystx.cloudsystem.library.elements.packets.out;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
public class PacketOutGlobalInfo extends Packet implements Serializable {

    private final NetworkConfig networkConfig;
    private final Map<ServiceGroup, List<Service>> services;

    public PacketOutGlobalInfo(NetworkConfig networkConfig, Map<ServiceGroup, List<Service>> services, List<CloudPlayer> cloudPlayers) {
        this.networkConfig = networkConfig;
        this.services = services;
        this.append("cloudPlayers", cloudPlayers);
    }

    public List<CloudPlayer> getCloudPlayers() {
        return this.document().getList("cloudPlayers", CloudPlayer.class);
    }
}
