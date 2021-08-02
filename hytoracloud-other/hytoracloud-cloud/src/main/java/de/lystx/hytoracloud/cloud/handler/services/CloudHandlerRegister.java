package de.lystx.hytoracloud.cloud.handler.services;

import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

public class CloudHandlerRegister implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketRegisterService) {
            PacketRegisterService packetRegisterService = (PacketRegisterService)packet;
            IService service = packetRegisterService.getIService();

            CloudDriver.getInstance().getServiceManager().registerService(service);

            CloudDriver.getInstance().sendPacket(new PacketOutRegisterServer(service));
            CloudDriver.getInstance().reload();
        }


    }
}
