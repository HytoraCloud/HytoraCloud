package de.lystx.hytoracloud.cloud.handler.services;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.service.IService;



public class CloudHandlerRegister implements IPacketHandler {

    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketRegisterService) {
            PacketRegisterService packetRegisterService = (PacketRegisterService)packet;
            IService service = packetRegisterService.getIService();

            CloudDriver.getInstance().getServiceManager().registerService(service);

            CloudDriver.getInstance().sendPacket(new PacketOutRegisterServer(service));
            CloudDriver.getInstance().reload();
        }


    }
}
