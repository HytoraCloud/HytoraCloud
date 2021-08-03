package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

public class BridgeHandlerServiceUpdate implements PacketHandler {



    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketServiceUpdate) {
            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;
            IService service = packetServiceUpdate.getService();

            CloudDriver.getInstance().getServiceManager().updateService(service);
        }
    }
}
