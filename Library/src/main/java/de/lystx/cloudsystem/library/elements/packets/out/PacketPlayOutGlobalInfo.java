package de.lystx.cloudsystem.library.elements.packets.out;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.stats.Statistics;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
public class PacketPlayOutGlobalInfo extends Packet implements Serializable {

    private final NetworkConfig networkConfig;
    private final Map<ServiceGroup, List<Service>> services;
    private final PermissionPool permissionPool;
    private final List<CloudPlayer> cloudPlayers;
    private final Statistics statistics;
    private final List<CloudSign> cloudSigns;
    private final String signLayOut;
    private final String npcs;

    public PacketPlayOutGlobalInfo(NetworkConfig networkConfig, Map<ServiceGroup, List<Service>> services, PermissionPool permissionPool, List<CloudPlayer> cloudPlayers, Statistics statistics, List<CloudSign> cloudSigns, String signLayOut, String npcs) {
        this.networkConfig = networkConfig;
        this.services = services;
        this.permissionPool = permissionPool;

        this.cloudPlayers = cloudPlayers;
        this.statistics = statistics;
        this.cloudSigns = cloudSigns;
        this.signLayOut = signLayOut;
        this.npcs = npcs;

    }
}
