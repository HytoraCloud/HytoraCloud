package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketShutdown;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;


@AllArgsConstructor @Getter
public class CloudHandlerShutdown implements PacketHandler {

    private final CloudSystem cloudSystem;

    public void handle(Packet packet) {
        if (packet instanceof PacketShutdown) {
            this.cloudSystem.shutdown();
        }
    }
}
