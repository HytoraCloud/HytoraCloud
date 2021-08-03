package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.other.PacketCallEvent;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;


import lombok.Getter;

@Getter
public class BridgeHandlerEvent implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCallEvent) {
            PacketCallEvent packetCallEvent = (PacketCallEvent)packet;

            if (CloudDriver.getInstance().getServiceManager().getThisService() != null && !packetCallEvent.getExcept().equalsIgnoreCase("null") && packetCallEvent.getExcept().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
                return;
            }

            CloudDriver.getInstance().getEventManager().callEvent(packetCallEvent.getIEvent());
        }
    }
}
