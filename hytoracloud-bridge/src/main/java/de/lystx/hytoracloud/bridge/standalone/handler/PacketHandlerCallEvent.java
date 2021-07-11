package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketCallEvent;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import de.lystx.hytoracloud.driver.service.managing.event.base.CloudEvent;
import lombok.Getter;

@Getter
public class PacketHandlerCallEvent implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketCallEvent) {
            PacketCallEvent packetCallEvent = (PacketCallEvent)packet;
            CloudEvent event = packetCallEvent.getCloudEvent();
            CloudDriver.getInstance().getEventService().callEvent(event);
        }
    }
}
