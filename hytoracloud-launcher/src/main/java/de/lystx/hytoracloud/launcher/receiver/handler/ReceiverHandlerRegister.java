package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

public class ReceiverHandlerRegister implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketOutRegisterServer) {
            PacketOutRegisterServer packetRegisterService = (PacketOutRegisterServer)packet;
            IService service = packetRegisterService.getService();
            //CloudDriver.getInstance().getServiceManager().registerService(service);
        }
    }
}
