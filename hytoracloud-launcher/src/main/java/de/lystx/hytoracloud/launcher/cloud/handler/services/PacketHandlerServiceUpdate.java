package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.Service;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


public class PacketHandlerServiceUpdate implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketServiceUpdate) {
            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;
            Service service = packetServiceUpdate.getService();
            CloudDriver.getInstance().getServiceManager().updateService(service);
        }
    }
}
