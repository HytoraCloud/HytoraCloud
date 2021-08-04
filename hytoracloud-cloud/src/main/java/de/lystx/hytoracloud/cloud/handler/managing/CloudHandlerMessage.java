package de.lystx.hytoracloud.cloud.handler.managing;

import de.lystx.hytoracloud.cloud.CloudSystem;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.both.PacketLogMessage;
import de.lystx.hytoracloud.driver.console.logger.LogService;
import lombok.AllArgsConstructor;


import de.lystx.hytoracloud.driver.connection.protocol.requests.ResponseStatus;

@AllArgsConstructor
public class CloudHandlerMessage implements IPacketHandler {

    private final CloudSystem cloudSystem;



    public void handle(IPacket packet) {
        if (packet instanceof PacketLogMessage) {
            PacketLogMessage packetLogMessage = (PacketLogMessage)packet;
            if (!packetLogMessage.isShowUpInConsole()) {
                CloudDriver.getInstance().getServiceRegistry().getInstance(LogService.class).log(packetLogMessage.getPrefix(), packetLogMessage.getMessage());
                return;
            }
            this.cloudSystem.getParent().getConsole().sendMessage(packetLogMessage.getPrefix(), packetLogMessage.getMessage());
        }
    }
}
