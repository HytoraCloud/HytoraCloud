package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.DefaultServiceManager;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.elements.service.Service;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

public class PacketHandlerRegister implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketRegisterService) {
            PacketRegisterService packetRegisterService = (PacketRegisterService)packet;
            String service = packetRegisterService.getService();
            Service service1 = ((DefaultServiceManager) CloudDriver.getInstance().getServiceManager()).registerService(service);
            packet.reply(component -> component.append(map -> map.put("service", service1)));
            CloudDriver.getInstance().reload();

        }
    }
}
