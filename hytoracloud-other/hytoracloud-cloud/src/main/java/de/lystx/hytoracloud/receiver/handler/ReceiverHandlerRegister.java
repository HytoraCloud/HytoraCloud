package de.lystx.hytoracloud.receiver.handler;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

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
