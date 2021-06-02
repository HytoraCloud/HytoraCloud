package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUnregisterPlayer;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.SneakyThrows;

public class PacketHandlerPlayer implements PacketHandler {

    @SneakyThrows
    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketUnregisterPlayer) {
            if (((PacketUnregisterPlayer) packet).getName() == null) {
                return;
            }
            CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(((PacketUnregisterPlayer) packet).getName());
            CloudDriver.getInstance().getCloudPlayerManager().unregisterPlayer(cloudPlayer);

            for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
                networkHandler.onPlayerQuit(cloudPlayer);
            }
        } else if (packet instanceof PacketUpdatePlayer) {
            PacketUpdatePlayer packetUpdatePlayer = (PacketUpdatePlayer) packet;
            CloudPlayer cloudPlayer = packetUpdatePlayer.getCloudPlayer();
            CloudDriver.getInstance().getCloudPlayerManager().update(cloudPlayer);
        }
    }
}
