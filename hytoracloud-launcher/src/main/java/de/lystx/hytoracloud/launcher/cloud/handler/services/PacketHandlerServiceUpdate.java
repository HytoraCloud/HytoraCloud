package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

public class PacketHandlerServiceUpdate implements PacketHandler {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketServiceUpdate) {
            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;
            Service service = packetServiceUpdate.getService();
            CloudDriver.getInstance().getServiceManager().updateService(service);
        }
    }
}
