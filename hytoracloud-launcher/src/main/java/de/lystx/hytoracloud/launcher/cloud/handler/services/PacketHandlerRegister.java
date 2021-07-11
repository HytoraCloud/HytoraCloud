package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.DefaultServiceManager;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.commons.service.Service;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;

public class PacketHandlerRegister implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketRegisterService) {
            PacketRegisterService packetRegisterService = (PacketRegisterService)packet;
            Service service = ((DefaultServiceManager) CloudDriver.getInstance().getServiceManager()).registerService(packetRegisterService.getService());

            packet.reply(ResponseStatus.SUCCESS, JsonEntity.toString(service));

            CloudDriver.getInstance().reload();

        }
    }
}
