package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


public class BridgeHandlerCommunication implements PacketHandler {


    
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketCommunication) {
            PacketCommunication packetCommunication = (PacketCommunication)packet;
            if (packetCommunication.isSendBack()) {
                CloudDriver.getInstance().sendPacket(packetCommunication.setSendBack(false));
            }
        }
    }

}
