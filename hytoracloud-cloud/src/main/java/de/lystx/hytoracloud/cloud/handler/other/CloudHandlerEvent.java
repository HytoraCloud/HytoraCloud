package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.both.other.PacketCallEvent;



import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class CloudHandlerEvent implements IPacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketCallEvent) {
            PacketCallEvent packetCallEvent = (PacketCallEvent)packet;

            if (!packetCallEvent.getExcept().equalsIgnoreCase("null") && packetCallEvent.getExcept().equalsIgnoreCase("cloud")) {
                return;
            }

            CloudDriver.getInstance().getEventManager().callEvent(packetCallEvent.getIEvent());
        }
    }
}
