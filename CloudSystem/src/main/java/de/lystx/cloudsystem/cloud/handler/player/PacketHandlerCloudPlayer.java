package de.lystx.cloudsystem.cloud.handler.player;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerChangeServerEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerJoinEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerQuitEvent;
import de.lystx.cloudsystem.library.elements.packets.both.PacketCallEvent;
import de.lystx.cloudsystem.library.elements.packets.both.PacketUpdatePlayer;
import de.lystx.cloudsystem.library.elements.packets.in.player.*;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;

import java.util.LinkedList;
import java.util.List;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.util.Constants;

public class PacketHandlerCloudPlayer extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;
    private int tries;
    private final List<String> list;

    public PacketHandlerCloudPlayer(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
        this.list = new LinkedList<>();
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInRegisterPlayer) {
            PacketInRegisterPlayer packetInRegisterPlayer = (PacketInRegisterPlayer) packet;
            CloudPlayer cloudPlayer = packetInRegisterPlayer.getCloudPlayer();
            Constants.EXECUTOR.callEvent(new CloudPlayerJoinEvent(cloudPlayer));
            cloudPlayer.setCloudPlayerData(this.cloudSystem.getService(PermissionService.class).getPermissionPool().getPlayerDataOrDefault(cloudPlayer.getName()));
            if (packetInRegisterPlayer.isSendMessage()) {
                if (!list.contains(cloudPlayer.getName())) {
                    list.add(cloudPlayer.getName());
                } else {
                    return;
                }
                if (!cloudSystem.isRunning() || (this.cloudSystem.getScreenPrinter().getScreen() != null && this.cloudSystem.getScreenPrinter().isInScreen())) {
                    return;
                }
                if (cloudPlayer.getServer().equalsIgnoreCase("no_server_found")) {
                    this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§7Player §b" + cloudPlayer.getName() + " §7is logging on on §a" + cloudPlayer.getProxy());
                } else {
                    this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§7Player §b" + cloudPlayer.getName() + " §7is connected on §a" + cloudPlayer.getServer() + " §7| §bProxy " + cloudPlayer.getProxy());
                }
                return;
            }
            if (this.cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(cloudPlayer.getName()) != null) {
                return;
            }
            if (!this.cloudSystem.getService(CloudPlayerService.class).registerPlayer(cloudPlayer)) {
                this.cloudSystem.getService(StatisticsService.class).getStatistics().add("registeredPlayers");
            }

            this.cloudSystem.getService(StatisticsService.class).getStatistics().add("connections");
           // this.cloudSystem.reload();

        } else if (packet instanceof PacketInPlayerExecuteCommand) {

            this.cloudSystem.getService(StatisticsService.class).getStatistics().add("executedCommands");

        } else if (packet instanceof PacketInNetworkPing) {

            this.cloudSystem.getService(StatisticsService.class).getStatistics().add("pings");

        } else if (packet instanceof PacketUpdatePlayer) {
            PacketUpdatePlayer player = (PacketUpdatePlayer)packet;
            PermissionPool permissionPool = cloudSystem.getService(PermissionService.class).getPermissionPool();
            permissionPool.updatePlayerData(player.getName(), player.getNewCloudPlayer().getCloudPlayerData());
            cloudSystem.getService(PermissionService.class).setPermissionPool(permissionPool);
            cloudSystem.getService(PermissionService.class).save();
            this.cloudSystem.getService(CloudPlayerService.class).update(player.getName(), player.getNewCloudPlayer());
            this.cloudSystem.reload();

       } else if (packet instanceof PacketInUnregisterPlayer) {
            PacketInUnregisterPlayer packetInUnregisterPlayer = (PacketInUnregisterPlayer)packet;
            CloudPlayer cloudPlayer = this
                    .cloudSystem
                    .getService(CloudPlayerService.class)
                    .getOnlinePlayer(packetInUnregisterPlayer.getName());
            if (cloudPlayer != null) {
                Constants.EXECUTOR.callEvent(new CloudPlayerQuitEvent(cloudPlayer));
                this.cloudSystem.getService(CloudPlayerService.class).removePlayer(cloudPlayer);
                //this.cloudSystem.reload();
            }
        }
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
