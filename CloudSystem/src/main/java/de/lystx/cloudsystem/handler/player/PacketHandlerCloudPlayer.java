package de.lystx.cloudsystem.handler.player;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInCloudPlayerOnline;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInCloudPlayerServerChange;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInPlayerExecuteCommand;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInRegisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.in.player.PacketPlayInUnregisterCloudPlayer;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayerJoin;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayerQuit;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayerServerChange;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;

import java.util.LinkedList;
import java.util.List;

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
        if (packet instanceof PacketPlayInRegisterCloudPlayer) {
            PacketPlayInRegisterCloudPlayer packetPlayInRegisterCloudPlayer = (PacketPlayInRegisterCloudPlayer) packet;
            CloudPlayer cloudPlayer = packetPlayInRegisterCloudPlayer.getCloudPlayer();

            if (packetPlayInRegisterCloudPlayer.isSendMessage()) {
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
            if (!cloudPlayer.getServer().equalsIgnoreCase("no_server_found")) {
                this.cloudSystem.getService(StatisticsService.class).getStatistics().add("connections");
            }
            this.cloudSystem.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutCloudPlayerJoin(cloudPlayer));
            this.cloudSystem.reload();
        } else if (packet instanceof PacketPlayInCloudPlayerOnline) {
            PacketPlayInCloudPlayerOnline packetPlayInCloudPlayerOnline = (PacketPlayInCloudPlayerOnline)packet;
            tries++;
            if (tries == cloudSystem.getService().getCloudProxies().size()) {
                if (!packetPlayInCloudPlayerOnline.isOnline()) {
                    cloudSystem.getService(CloudPlayerService.class).removePlayer(cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(packetPlayInCloudPlayerOnline.getPlayerName()));
                    cloudSystem.reload();
                }
            }
        } else if (packet instanceof PacketPlayInPlayerExecuteCommand) {
            this.cloudSystem.getService(StatisticsService.class).getStatistics().add("executedCommands");
            this.cloudSystem.reload();
       } else if (packet instanceof PacketPlayInUnregisterCloudPlayer) {
            PacketPlayInUnregisterCloudPlayer packetPlayInUnregisterCloudPlayer = (PacketPlayInUnregisterCloudPlayer)packet;
            CloudPlayer cloudPlayer = this
                    .cloudSystem
                    .getService(CloudPlayerService.class)
                    .getOnlinePlayer(packetPlayInUnregisterCloudPlayer.getName());
            if (cloudPlayer != null) {
                this.cloudSystem.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutCloudPlayerQuit(cloudPlayer));
                this.cloudSystem.getService(CloudPlayerService.class).removePlayer(cloudPlayer);
                this.cloudSystem.reload();

                if (!cloudSystem.isRunning() || (this.cloudSystem.getScreenPrinter().getScreen() != null && this.cloudSystem.getScreenPrinter().isInScreen())) {
                    return;
                }
                if (!list.contains(cloudPlayer.getName())) {
                    return;
                }
                list.remove(cloudPlayer.getName());
                if (cloudPlayer.getServer().equalsIgnoreCase("no_server_found")) {
                    this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§cPlayer §e" + cloudPlayer.getName() + " §ccouldnt be logged in!");
                } else {
                    this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§7Player §b" + cloudPlayer.getName() + " §7is disconnected from §a" + cloudPlayer.getServer());
                }

            }
        } else if (packet instanceof PacketPlayInCloudPlayerServerChange) {
            PacketPlayInCloudPlayerServerChange packetPlayInCloudPlayerServerChange = (PacketPlayInCloudPlayerServerChange)packet;
            try {
                CloudPlayer cloudPlayer = this.cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(packetPlayInCloudPlayerServerChange.getCloudPlayer().getName());
                if (cloudPlayer != null) {
                    this.cloudSystem.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutCloudPlayerServerChange(cloudPlayer, packetPlayInCloudPlayerServerChange.getNewServer()));
                    cloudPlayer.setServer(packetPlayInCloudPlayerServerChange.getNewServer());
                    this.cloudSystem.getService(CloudPlayerService.class).update(cloudPlayer.getName(), cloudPlayer);
                    this.cloudSystem.reload();

                    /*CloudPlayer newCloudPlayer = new CloudPlayer(
                            cloudPlayer.getName(),
                            cloudPlayer.getUuid(),
                            cloudPlayer.getIpAddress(),
                            packetPlayInCloudPlayerServerChange.getNewServer(),
                            cloudPlayer.getProxy()
                    );
                    this.cloudSystem.getService(CloudPlayerService.class).removePlayer(cloudPlayer); //Remove old player
                    this.cloudSystem.getService(CloudPlayerService.class).registerPlayer(newCloudPlayer); //Add new player
                    this.cloudSystem.reload(); //Update players for all services*/
                }
            } catch (NullPointerException e) {}

        }
    }
}
