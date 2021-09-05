package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.in.PacketShutdown;
import lombok.AllArgsConstructor;
import lombok.Getter;




@AllArgsConstructor @Getter
public class CloudHandlerShutdown implements IPacketHandler {

    private final CloudSystem cloudSystem;

    public void handle(IPacket packet) {
        if (packet instanceof PacketShutdown) {
            this.cloudSystem.shutdown();
        }
    }
}
