package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

public class ReceiverHandlerRegister implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketRegisterService) {
            PacketRegisterService packetRegisterService = (PacketRegisterService)packet;
            String service = packetRegisterService.getService();
            CloudSideServiceManager cloudSideServiceManager = (CloudSideServiceManager) CloudDriver.getInstance().getServiceManager();
            cloudSideServiceManager.registerService(service, packet);
        }
    }
}
