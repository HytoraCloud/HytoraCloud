package de.lystx.cloudsystem.cloud.handler.player;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerChangeServerEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerJoinEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerQuitEvent;
import de.lystx.cloudsystem.library.elements.list.Filter;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCallEvent;
import de.lystx.cloudsystem.library.elements.packets.both.player.PacketUpdatePlayer;
import de.lystx.cloudsystem.library.elements.packets.both.player.PacketRegisterPlayer;
import de.lystx.cloudsystem.library.elements.packets.both.player.PacketUnregisterPlayer;
import de.lystx.cloudsystem.library.elements.packets.in.player.*;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;

import java.util.LinkedList;
import java.util.List;

import de.lystx.cloudsystem.library.Cloud;

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
    public void handle(PacketUpdatePlayer packet) {
        PermissionPool permissionPool = cloudSystem.getService(PermissionService.class).getPermissionPool();
        permissionPool.updatePlayerData(packet.getClass().getSimpleName(), packet.getNewCloudPlayer().getData());
        cloudSystem.getService(PermissionService.class).setPermissionPool(permissionPool);
        cloudSystem.getService(PermissionService.class).save();
        this.cloudSystem.getService(CloudPlayerService.class).update(packet.getClass().getSimpleName(), packet.getNewCloudPlayer());
        this.cloudSystem.reload();
        Cloud.getInstance().setCloudPlayerFilter(new Filter<>(this.cloudSystem.getService(CloudPlayerService.class).getOnlinePlayers()));
    }

    @PacketHandler
    public void handle(PacketUnregisterPlayer packet) {
        CloudPlayer cloudPlayer = this
                .cloudSystem
                .getService(CloudPlayerService.class)
                .getOnlinePlayer(packet.getClass().getSimpleName());
        if (cloudPlayer != null) {
            Cloud.getInstance().getCurrentCloudExecutor().callEvent(new CloudPlayerQuitEvent(cloudPlayer));
            this.cloudSystem.getService(CloudPlayerService.class).removePlayer(cloudPlayer);
            this.cloudSystem.reload();
        }
    }

    @PacketHandler
    public void handle(PacketRegisterPlayer packet) {
        //Constants.SERVICE_FILTER = new Filter<>(this.cloudSystem.getService().allServices());

        CloudPlayer cloudPlayer = packet.getCloudPlayer();
        cloudSystem.callEvent(new CloudPlayerJoinEvent(cloudPlayer));
        cloudPlayer.setCloudPlayerData(this.cloudSystem.getService(PermissionService.class).getPermissionPool().getPlayerDataOrDefault(cloudPlayer.getName()));


        if (packet.isSendMessage()) {
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
            this.cloudSystem.getService(PermissionService.class).getPermissionPool().update();
        }

        this.cloudSystem.getService(StatisticsService.class).getStatistics().add("connections");
    }


    @PacketHandler
    public void handleEvent(PacketCallEvent event) {
        if (event.getEvent() instanceof CloudPlayerChangeServerEvent) {
            CloudPlayerChangeServerEvent serverEvent = (CloudPlayerChangeServerEvent)event.getEvent();
            try {
                CloudPlayer cloudPlayer = this.cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(serverEvent.getCloudPlayer().getName());
                if (cloudPlayer != null) {
                    cloudPlayer.setService(cloudSystem.getService().getService(serverEvent.getNewServer()));
                    cloudPlayer.update();
                }
            } catch (NullPointerException e) {
                //IGNORING
            }
        }
    }
}
