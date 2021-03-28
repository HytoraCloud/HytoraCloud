package de.lystx.cloudsystem.cloud.handler.player;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerChangeServerEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerJoinEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerQuitEvent;
import de.lystx.cloudsystem.library.elements.list.Filter;
import de.lystx.cloudsystem.library.elements.packets.both.PacketCallEvent;
import de.lystx.cloudsystem.library.elements.packets.both.PacketUpdatePermissionPool;
import de.lystx.cloudsystem.library.elements.packets.both.PacketUpdatePlayer;
import de.lystx.cloudsystem.library.elements.packets.in.player.*;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;

import java.util.LinkedList;
import java.util.List;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.util.Constants;

public class PacketHandlerCloudPlayer {

    private final CloudSystem cloudSystem;
    private int tries;
    private final List<String> list;

    public PacketHandlerCloudPlayer(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
        this.list = new LinkedList<>();
    }


    @PacketHandler
    public void handle(PacketInPlayerExecuteCommand packet) {
        this.cloudSystem.getService(StatisticsService.class).getStatistics().add("executedCommands");
    }

    @PacketHandler
    public void handle(PacketInNetworkPing packet) {
        this.cloudSystem.getService(StatisticsService.class).getStatistics().add("pings");
    }

    @PacketHandler
    public void handle(PacketUpdatePlayer packet) {
        PermissionPool permissionPool = cloudSystem.getService(PermissionService.class).getPermissionPool();
        permissionPool.updatePlayerData(((PacketUpdatePlayer)packet).getName(), ((PacketUpdatePlayer)packet).getNewCloudPlayer().getCloudPlayerData());
        cloudSystem.getService(PermissionService.class).setPermissionPool(permissionPool);
        cloudSystem.getService(PermissionService.class).save();
        this.cloudSystem.getService(CloudPlayerService.class).update(((PacketUpdatePlayer)packet).getName(), ((PacketUpdatePlayer)packet).getNewCloudPlayer());
        this.cloudSystem.reload();
    }

    @PacketHandler
    public void handle(PacketInUnregisterPlayer packet) {
        CloudPlayer cloudPlayer = this
                .cloudSystem
                .getService(CloudPlayerService.class)
                .getOnlinePlayer(((PacketInUnregisterPlayer)packet).getName());
        if (cloudPlayer != null) {
            Constants.EXECUTOR.callEvent(new CloudPlayerQuitEvent(cloudPlayer));
            this.cloudSystem.getService(CloudPlayerService.class).removePlayer(cloudPlayer);
            //this.cloudSystem.reload();
        }
    }

    @PacketHandler
    public void handle(PacketInRegisterPlayer packet) {
        Constants.SERVICE_FILTER = new Filter<>(this.cloudSystem.getService().allServices());

        CloudPlayer cloudPlayer = ((PacketInRegisterPlayer) packet).getCloudPlayer();
        Constants.EXECUTOR.callEvent(new CloudPlayerJoinEvent(cloudPlayer));
        cloudPlayer.setCloudPlayerData(this.cloudSystem.getService(PermissionService.class).getPermissionPool().getPlayerDataOrDefault(cloudPlayer.getName()));
        if (((PacketInRegisterPlayer) packet).isSendMessage()) {
            if (!list.contains(cloudPlayer.getName())) {
                list.add(cloudPlayer.getName());
            } else {
                return;
            }
        }
        if (this.cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(cloudPlayer.getName()) != null) {
            return;
        }
        if (!this.cloudSystem.getService(CloudPlayerService.class).registerPlayer(cloudPlayer)) {
            this.cloudSystem.getService(StatisticsService.class).getStatistics().add("registeredPlayers");
            this.cloudSystem.sendPacket(new PacketUpdatePermissionPool(this.cloudSystem.getService(PermissionService.class).getPermissionPool()).setSendBack(false));
        }

        this.cloudSystem.getService(StatisticsService.class).getStatistics().add("connections");
       // this.cloudSystem.reload();

    }


    @PacketHandler
    public void handleEvent(PacketCallEvent event) {
        if (event.getEvent() instanceof CloudPlayerChangeServerEvent) {
            CloudPlayerChangeServerEvent serverEvent = (CloudPlayerChangeServerEvent)event.getEvent();
            CloudPlayer cloudPlayer = this.cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(serverEvent.getCloudPlayer().getName());
            if (cloudPlayer != null) {
                cloudPlayer.setServer(serverEvent.getNewServer());
                this.cloudSystem.getService(CloudPlayerService.class).update(cloudPlayer.getName(), cloudPlayer);
            }
        }
    }
}
