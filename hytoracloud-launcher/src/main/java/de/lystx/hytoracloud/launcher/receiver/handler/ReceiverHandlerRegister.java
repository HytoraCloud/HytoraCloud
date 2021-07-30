package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

public class ReceiverHandlerRegister implements PacketHandler {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketOutRegisterServer) {
            PacketOutRegisterServer packetRegisterService = (PacketOutRegisterServer)packet;
            IService service = packetRegisterService.getService();
            //CloudDriver.getInstance().getServiceManager().registerService(service);
        }
    }
}
