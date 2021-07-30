package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketCallEvent;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;


import lombok.Getter;

@Getter
public class BridgeHandlerEvent implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCallEvent) {
            PacketCallEvent packetCallEvent = (PacketCallEvent)packet;

            if (CloudDriver.getInstance().getCurrentService() != null && !packetCallEvent.getExcept().equalsIgnoreCase("null") && packetCallEvent.getExcept().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
                return;
            }

            CloudDriver.getInstance().getEventService().callEvent(packetCallEvent.getCloudEvent());
        }
    }
}
