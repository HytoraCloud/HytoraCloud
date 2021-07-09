package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketTPS;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class PacketHandlerTPS implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketTPS) {
            PacketTPS packetTPS = (PacketTPS)packet;
            if (packetTPS.getTps() == null) {
                return;
            }
            CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(packetTPS.getPlayer());
            cloudPlayer.sendMessage("  §8» §b" + packetTPS.getService().getName() + " §8┃ §7" + packetTPS.getTps());
        }
    }
}
