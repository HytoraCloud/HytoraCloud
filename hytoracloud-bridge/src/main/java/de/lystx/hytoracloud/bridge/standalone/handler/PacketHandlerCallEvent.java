package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketCallEvent;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import lombok.Getter;

@Getter
public class PacketHandlerCallEvent implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCallEvent) {
            PacketCallEvent packetCallEvent = (PacketCallEvent)packet;
            CloudEvent event = packetCallEvent.getCloudEvent();
            CloudDriver.getInstance().getEventService().callEvent(event);
        }
    }
}
