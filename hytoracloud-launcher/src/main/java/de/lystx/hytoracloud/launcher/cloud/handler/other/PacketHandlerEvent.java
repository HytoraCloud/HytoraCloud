package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketCallEvent;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PacketHandlerEvent implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketCallEvent) {
            PacketCallEvent packetCallEvent = (PacketCallEvent)packet;
            CloudDriver.getInstance().getEventService().callEvent(packetCallEvent.getCloudEvent());
        }
    }
}
