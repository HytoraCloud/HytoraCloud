package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketShutdown;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor @Getter
public class PacketHandlerShutdown implements PacketHandler {

    private final CloudSystem cloudSystem;

    public void handle(Packet packet) {
        if (packet instanceof PacketShutdown) {
            this.cloudSystem.shutdown();
        }
    }
}
