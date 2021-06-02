package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.elements.packets.both.service.ServicePacket;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

public class PacketHandlerCommunication implements PacketHandler {


    
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunication) {
            PacketCommunication packetCommunication = (PacketCommunication)packet;
            if (packetCommunication.isSendBack()) {
                CloudDriver.getInstance().sendPacket(packetCommunication.setSendBack(false));
            }
        } else if (packet instanceof ServicePacket && ((ServicePacket) packet).getService().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getName())) {

            Packet servicePacket = ((ServicePacket) packet).getPacket();
            CloudDriver.getInstance().getConnection().getPacketAdapter().handle(servicePacket);
        }
    }

}
