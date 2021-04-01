package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.both.player.PacketUpdatePlayer;
import de.lystx.cloudsystem.library.elements.packets.both.player.PacketRegisterPlayer;
import de.lystx.cloudsystem.library.elements.packets.both.player.PacketUnregisterPlayer;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerPlayer {


    private final CloudAPI cloudAPI;

    @PacketHandler
    public void handle(PacketUpdatePlayer packet) {
        final CloudPlayer cloudPlayer = packet.getNewCloudPlayer();
        cloudAPI.getCloudPlayers().update(cloudPlayer);
    }

    @PacketHandler
    public void handle(PacketRegisterPlayer packet) {
        CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(packet.getCloudPlayer().getName());
        if (cloudPlayer == null) {
            cloudAPI.getCloudPlayers().add(packet.getCloudPlayer());
        }
        for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
            networkHandler.onPlayerJoin(packet.getCloudPlayer());
        }
    }

    @PacketHandler
    public void handle(PacketUnregisterPlayer packet) {
        CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(packet.getName());
        if (cloudPlayer != null) {
            cloudAPI.getCloudPlayers().remove(cloudPlayer);
        }
        for (NetworkHandler networkHandler : this.cloudAPI.getCloudClient().getNetworkHandlers()) {
            networkHandler.onPlayerQuit(cloudPlayer);
        }
    }

}
