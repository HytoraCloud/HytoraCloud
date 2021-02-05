package de.lystx.cloudsystem.library.elements.packets.wrapper;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

import java.util.List;

@Getter
public class WrapperPacketGlobalInfo extends Packet {

    private final NetworkConfig networkConfig;
    private final List<ServiceGroup> groups;
    private final List<CloudPlayer> cloudPlayers;
    private final List<Service> services;

    public WrapperPacketGlobalInfo(NetworkConfig networkConfig, List<ServiceGroup> groups, List<CloudPlayer> cloudPlayers, List<Service> services) {
        this.networkConfig = networkConfig;
        this.groups = groups;
        this.cloudPlayers = cloudPlayers;
        this.services = services;
    }
}
