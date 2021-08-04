package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.both.other.PacketCallEvent;




import lombok.Getter;

@Getter
public class BridgeHandlerEvent implements IPacketHandler {


    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketCallEvent) {
            PacketCallEvent packetCallEvent = (PacketCallEvent)packet;

            if (CloudDriver.getInstance().getServiceManager().getThisService() != null && !packetCallEvent.getExcept().equalsIgnoreCase("null") && packetCallEvent.getExcept().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
                return;
            }

            CloudDriver.getInstance().getEventManager().callEvent(packetCallEvent.getIEvent());
        }
    }
}
