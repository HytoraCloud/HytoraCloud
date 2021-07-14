package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketCallEvent;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import lombok.Getter;

@Getter
public class BridgeHandlerEvent implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketCallEvent) {
            PacketCallEvent packetCallEvent = (PacketCallEvent)packet;

            if (!packetCallEvent.getExcept().equalsIgnoreCase("null") && packetCallEvent.getExcept().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
                return;
            }

            CloudDriver.getInstance().getEventService().callEvent(packetCallEvent.getCloudEvent());
        }
    }
}
