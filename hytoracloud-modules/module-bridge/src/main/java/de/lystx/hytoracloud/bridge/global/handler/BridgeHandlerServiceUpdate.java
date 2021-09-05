package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.service.IService;



public class BridgeHandlerServiceUpdate implements IPacketHandler {



    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketServiceUpdate) {
            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;
            IService service = packetServiceUpdate.getService();

            CloudDriver.getInstance().getServiceManager().updateService(service);
        }
    }
}
