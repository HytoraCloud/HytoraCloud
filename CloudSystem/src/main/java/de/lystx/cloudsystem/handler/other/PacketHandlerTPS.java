package de.lystx.cloudsystem.handler.other;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketPlayOutTPS;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PacketHandlerTPS extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutTPS) {
            PacketPlayOutTPS packetPlayOutTPS = (PacketPlayOutTPS)packet;
            if (packetPlayOutTPS.getTps() == null) {
                return;
            }
            CloudPlayer cloudPlayer = cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(packetPlayOutTPS.getPlayer());
            cloudPlayer.sendMessage(cloudSystem.getService(CloudNetworkService.class).getCloudServer(), "  §8» §b" + packetPlayOutTPS.getService().getName() + " §8┃ §7" + packetPlayOutTPS.getTps());
        }
    }
}
