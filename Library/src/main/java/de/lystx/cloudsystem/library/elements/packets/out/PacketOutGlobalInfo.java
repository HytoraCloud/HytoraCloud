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
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
public class PacketOutGlobalInfo extends Packet implements Serializable {

    private final NetworkConfig networkConfig;
    private final Map<ServiceGroup, List<Service>> services;

    public PacketOutGlobalInfo(NetworkConfig networkConfig, Map<ServiceGroup, List<Service>> services, PermissionPool permissionPool, List<CloudPlayer> cloudPlayers, List<CloudSign> cloudSigns, VsonObject signLayOut) {
        this.networkConfig = networkConfig;
        this.services = services;


        this.append("cloudSigns", cloudSigns);
        this.append("cloudPlayers", cloudPlayers);
        this.append("signLayOut", signLayOut.toString());
        try {
            this.append("permissionPool", new Document()
                    .append("enabled", permissionPool.isEnabled())
                    .append("cache", permissionPool.getPlayerCache())
                    .append("groups", permissionPool.getPermissionGroups())
                    .getJsonObject()
            );
        } catch (Exception e) {}

    }


    public String getSignLayOut() {
        return this.document().getString("signLayOut");
    }


    public PermissionPool getPermissionPool() {
        PermissionPool permissionPool = new PermissionPool(null);
        permissionPool.setEnabled(this.document().getDocument("permissionPool").getBoolean("enabled"));
        permissionPool.setPermissionGroups(this.document().getDocument("permissionPool").getList("groups", PermissionGroup.class));
        permissionPool.setPlayerCache(this.document().getDocument("permissionPool").getList("cache", CloudPlayerData.class));
        return permissionPool;
    }

    public List<CloudPlayer> getCloudPlayers() {
        return this.document().getList("cloudPlayers", CloudPlayer.class);
    }

    public List<CloudSign> getCloudSigns() {
        return this.document().getList("cloudSigns", CloudSign.class);
    }
}
