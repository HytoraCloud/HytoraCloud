package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketCallEvent;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;


import de.lystx.hytoracloud.driver.service.event.CloudEvent;
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
