package de.lystx.hytoracloud.cloud.handler.services;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.wrapped.ServiceObject;

import java.util.function.Consumer;

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
