package de.lystx.cloudsystem.cloud.handler.other;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketTPS;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@AllArgsConstructor
@Getter
public class PacketHandlerTPS extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketTPS) {
            PacketTPS packetTPS = (PacketTPS)packet;
            if (packetTPS.getTps() == null) {
                return;
            }
            CloudPlayer cloudPlayer = cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(packetTPS.getPlayer());
            cloudPlayer.sendMessage("  §8» §b" + packetTPS.getService().getName() + " §8┃ §7" + packetTPS.getTps());
        }
    }
}
