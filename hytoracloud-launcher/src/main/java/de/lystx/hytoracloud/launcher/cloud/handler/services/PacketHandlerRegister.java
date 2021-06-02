package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.launcher.cloud.impl.manager.DefaultServiceManager;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

public class PacketHandlerRegister implements PacketHandler {



    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketRegisterService) {
            PacketRegisterService packetRegisterService = (PacketRegisterService)packet;
            Service service = packetRegisterService.getService();
            ((DefaultServiceManager) CloudDriver.getInstance().getServiceManager()).registerService(service);
            CloudDriver.getInstance().reload();

        }
    }
}
