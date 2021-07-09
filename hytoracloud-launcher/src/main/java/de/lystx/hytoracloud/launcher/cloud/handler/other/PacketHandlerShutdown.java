package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketShutdown;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


@AllArgsConstructor @Getter
public class PacketHandlerShutdown implements PacketHandler {

    private final CloudSystem cloudSystem;

    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketShutdown) {
            this.cloudSystem.shutdown();
        }
    }
}
