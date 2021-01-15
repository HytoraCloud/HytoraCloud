package de.lystx.cloudsystem.handler.player;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInCloudPlayerServerChange;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInPlayerExecuteCommand;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInRegisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInUnregisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayers;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutForceRegisterPlayer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;

import java.util.UUID;

public class PacketHandlerCloudPlayer extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerCloudPlayer(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInRegisterCloudPlayer) {
            PacketPlayInRegisterCloudPlayer packetPlayInRegisterCloudPlayer = (PacketPlayInRegisterCloudPlayer) packet;
            if (this.cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(packetPlayInRegisterCloudPlayer.getUuid()) != null) {
                return;
            }
            CloudPlayer cloudPlayer = new CloudPlayer(packetPlayInRegisterCloudPlayer.getName(), packetPlayInRegisterCloudPlayer.getUuid(), packetPlayInRegisterCloudPlayer.getIpAddress(), packetPlayInRegisterCloudPlayer.getCurrentServer(), packetPlayInRegisterCloudPlayer.getCurrentProxy());
            if (!this.cloudSystem.getService(CloudPlayerService.class).registerPlayer(cloudPlayer)) {
                this.cloudSystem.getService(StatisticsService.class).getStatistics().add("registeredPlayers");
                this.cloudSystem.reload("statistics");
            }
            this.cloudSystem.getService(StatisticsService.class).getStatistics().add("connections");
            this.cloudSystem.reload("statistics");
            this.cloudSystem.reload("cloudPlayers");
            if (!cloudSystem.isRunning()) {
                return;
            }

            Service service = this.cloudSystem.getService().getService(cloudPlayer.getServer());
            this.cloudSystem.getService(CloudPlayerService.class).setOnlinePlayerState(service, cloudPlayer, true);
            this.cloudSystem.getService(CloudPlayerService.class).reloadOnlinePlayers();
            this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§7Player §b" + cloudPlayer.getName() + " §7is connected on §a" + cloudPlayer.getServer() + " §7| §bProxy " + cloudPlayer.getProxy());
        } else if (packet instanceof PacketPlayInPlayerExecuteCommand) {
            PacketPlayInPlayerExecuteCommand packetPlayInPlayerExecuteCommand = (PacketPlayInPlayerExecuteCommand)packet;

            this.cloudSystem.getService(StatisticsService.class).getStatistics().add("executedCommands");
            this.cloudSystem.reload("statistics");
       } else if (packet instanceof PacketPlayInUnregisterCloudPlayer) {
            PacketPlayInUnregisterCloudPlayer packetPlayInUnregisterCloudPlayer = (PacketPlayInUnregisterCloudPlayer)packet;
            UUID uuid = packetPlayInUnregisterCloudPlayer.getUuid();
            CloudPlayer cloudPlayer = this.cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(uuid);
            if (cloudPlayer == null) {
                if (!cloudSystem.isRunning()) {
                    return;
                }
                this.cloudSystem.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutForceRegisterPlayer(uuid));
                //this.cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cTried to unregister a cloudplayer who isn't registered! Recaching player...");
                return;
            }
            this.cloudSystem.getService(CloudPlayerService.class).removePlayer(cloudPlayer);

            Service service = this.cloudSystem.getService().getService(cloudPlayer.getServer());
            this.cloudSystem.getService(CloudPlayerService.class).setOnlinePlayerState(service, cloudPlayer, false);
            this.cloudSystem.getService(CloudPlayerService.class).reloadOnlinePlayers();

             this.cloudSystem.reload("cloudPlayers");
            if (!cloudSystem.isRunning()) {
                return;
            }
            this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§7Player §b" + cloudPlayer.getName() + " §7is disconnected from §a" + cloudPlayer.getServer());

        } else if (packet instanceof PacketPlayInCloudPlayerServerChange) {
            PacketPlayInCloudPlayerServerChange packetPlayInCloudPlayerServerChange = (PacketPlayInCloudPlayerServerChange)packet;
            UUID uuid = packetPlayInCloudPlayerServerChange.getUuid();
            CloudPlayer cloudPlayer = this.cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(uuid);
            if (cloudPlayer == null) {
                if (!cloudSystem.isRunning()) {
                    return;
                }
                this.cloudSystem.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutForceRegisterPlayer(uuid));
                //this.cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cTried to change server of a cloudplayer who isn't registered! Recaching player...");
                return;
            }

            CloudPlayer newCloudPlayer = new CloudPlayer(cloudPlayer.getName(), cloudPlayer.getUuid(), cloudPlayer.getIpAddress(), packetPlayInCloudPlayerServerChange.getNewServer(), cloudPlayer.getProxy());
            this.cloudSystem.getService(CloudPlayerService.class).removePlayer(cloudPlayer);
            this.cloudSystem.getService(CloudPlayerService.class).registerPlayer(newCloudPlayer);

            Service service = this.cloudSystem.getService().getService(cloudPlayer.getServer());
            this.cloudSystem.getService(CloudPlayerService.class).setOnlinePlayerState(service, cloudPlayer, false);
            this.cloudSystem.getService(CloudPlayerService.class).setOnlinePlayerState(service, newCloudPlayer, true);
            this.cloudSystem.getService(CloudPlayerService.class).reloadOnlinePlayers();
             this.cloudSystem.reload("cloudPlayers");
        }
    }
}
