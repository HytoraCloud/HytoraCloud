package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.commons.service.IService;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

public class CloudHandlerRegister implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketRegisterService) {
            PacketRegisterService packetRegisterService = (PacketRegisterService)packet;
            IService service = ((CloudSideServiceManager) CloudDriver.getInstance().getServiceManager()).registerService(packetRegisterService.getService(), packet);
            packet.reply(component -> component.put("service", service));

            CloudDriver.getInstance().sendPacket(new PacketOutRegisterServer(service));
            CloudDriver.getInstance().reload();

        }


    }
}
