package de.lystx.hytoracloud.cloud.handler.managing;

import de.lystx.hytoracloud.cloud.CloudSystem;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.PacketLogMessage;
import de.lystx.hytoracloud.driver.console.logger.LogService;
import lombok.AllArgsConstructor;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.response.ResponseStatus;

@AllArgsConstructor
public class CloudHandlerMessage implements PacketHandler {

    private final CloudSystem cloudSystem;


    
    public void handle(Packet packet) {
        if (packet instanceof PacketLogMessage) {
            PacketLogMessage packetLogMessage = (PacketLogMessage)packet;
            if (!packetLogMessage.isShowUpInConsole()) {
                packet.reply(ResponseStatus.CONFLICT, "Not show up in console");
                CloudDriver.getInstance().getServiceRegistry().getInstance(LogService.class).log(packetLogMessage.getPrefix(), packetLogMessage.getMessage());
                return;
            }
            this.cloudSystem.getParent().getConsole().sendMessage(packetLogMessage.getPrefix(), packetLogMessage.getMessage());
            packet.reply(ResponseStatus.SUCCESS, "Showed up in console");
        }
    }
}
