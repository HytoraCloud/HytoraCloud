package de.lystx.cloudsystem.library.elements.packets.out;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.stats.Statistics;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCConfig;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
public class PacketPlayOutGlobalInfo extends Packet implements Serializable {

    private final NetworkConfig networkConfig;
    private final Map<ServiceGroup, List<Service>> services;

    public PacketPlayOutGlobalInfo(NetworkConfig networkConfig, Map<ServiceGroup, List<Service>> services, PermissionPool permissionPool, List<CloudPlayer> cloudPlayers, Statistics statistics, List<CloudSign> cloudSigns, Document signLayOut, Document npcs, NPCConfig npcConfig) {
        this.networkConfig = networkConfig;
        this.services = services;


        this.append("cloudSigns", cloudSigns);
        this.append("cloudPlayers", cloudPlayers);
        this.append("stats", statistics.toDocument().getJsonObject());
        this.append("signLayOut", signLayOut.toString());
        this.append("npcs", npcs.toString());
        this.append("npcConfig", npcConfig);
        try {
            this.append("permissionPool", new Document()
                    .append("enabled", permissionPool.isEnabled())
                    .append("cache", permissionPool.getPlayerCache())
                    .append("groups", permissionPool.getPermissionGroups())
                    .getJsonObject()
            );
        } catch (Exception e) {}

    }

    public String getNpcs() {
        return this.document().getString("npcs");
    }

    public String getSignLayOut() {
        return this.document().getString("signLayOut");
    }

    public NPCConfig getNpcConfig() {
        return this.document().getObject("npcConfig", NPCConfig.class);
    }


    public PermissionPool getPermissionPool() {
        PermissionPool permissionPool = new PermissionPool(null);
        permissionPool.setEnabled(this.document().getDocument("permissionPool").getBoolean("enabled"));
        permissionPool.setPermissionGroups(this.document().getDocument("permissionPool").getList("groups", PermissionGroup.class));
        permissionPool.setPlayerCache(this.document().getDocument("permissionPool").getList("cache", CloudPlayerData.class));
        return permissionPool;
    }

    public Statistics getStatistics() {
        Statistics statistics = new Statistics();
        statistics.load(this.document().getDocument("stats"));
        return statistics;
    }

    public List<CloudPlayer> getCloudPlayers() {
        return this.document().getList("cloudPlayers", CloudPlayer.class);
    }

    public List<CloudSign> getCloudSigns() {
        return this.document().getList("cloudSigns", CloudSign.class);
    }
}
