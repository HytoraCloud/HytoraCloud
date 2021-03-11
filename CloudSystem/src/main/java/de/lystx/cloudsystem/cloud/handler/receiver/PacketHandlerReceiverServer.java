package de.lystx.cloudsystem.cloud.handler.receiver;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketHandlerReceiverServer {

    private final CloudSystem cloudSystem;


    @PacketHandler
    public void handleStartNew(Packet packet) {
        if (packet.getClass().getSimpleName().startsWith("PacketPlayIn")) {
            this.cloudSystem.sendPacket(packet);
        }
    }
}
